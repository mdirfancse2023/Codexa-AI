const http = require('http');
const httpProxy = require('http-proxy');
const Redis = require('ioredis');
const redisUrl = process.env.REDIS_URL || 'redis://redis-service:6379';

const redis = new Redis(redisUrl, {
    maxRetriesPerRequest: null,
    enableReadyCheck: false,
    retryStrategy(times) {
        const delay = Math.min(times * 50, 2000);
        console.log(`Redis connection failed. Retrying in ${delay}ms...`);
        return delay;
    }
});

redis.on('error', (err) => {
    console.error('Redis Client error:', err.message);
});

redis.on('connect', () => {
    console.log('Connected to Redis successfully.');
});

const proxy = httpProxy.createProxyServer({
    ws: true,
    changeOrigin: true,
    xfwd: true
});

async function getTarget(hostname) {
    try {
        const targetIp = await redis.get(`route:${hostname}`);
        if(targetIp) {
            return targetIp;
        }
    } catch (err) {
        console.error('Error fetching target from Redis:', err.message);
    }
    return null;
}

// HELPER: Ensures the target URL is properly formatted
const getTargetUrl = (ip) => {
    return ip.includes(':') ? `http://${ip}` : `http://${ip}:5173`;
}

const server = http.createServer(async (req, res) => {
    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0]; // Remove port if present

    const targetIp = await getTarget(hostname);
    if (!targetIp) {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        return res.end(`Preview not found for ${hostname}.`);
    }

    const target = getTargetUrl(targetIp); //http://10.244.0.7:5173
    console.log(`HTTP Proxy: ${hostname} -> ${target}${req.url}`);

    // Here we can add authentication layer to check if the request is authorized to access the target

    proxy.web(req, res, { target }, (e) => {
        console.error(`Proxy error for ${hostname}:`, e.message);
        if(!res.headersSent) {
            res.writeHead(502, { 'Content-Type': 'text/plain' });
            res.end(`Bad Gateway: Unable to proxy to ${hostname}.`);
        }
    });
});

server.on('upgrade', async (req, socket, head) => {
    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0]; // Remove port if present
    const targetIp = await getTarget(hostname);
    if(targetIp) {
        const target = getTargetUrl(targetIp);
        console.log(`WebSocket Upgrade: ${hostname} -> ${target}`);
        proxy.ws(req, socket, head, { target }, (e) => {
            console.error(`WebSocket proxy error for ${hostname}:`, e.message);
            socket.destroy();
        });
    } else {
        console.warn(`WebSocket Upgrade failed: No target for ${hostname}`);
        socket.destroy();
    }
});

server.listen(80, () => {
    console.log('Proxy server is running on port 80');
});