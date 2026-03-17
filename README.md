# Codexa AI

Codexa AI is an AI-assisted software workspace for creating, editing, previewing, and deploying projects from a chat-driven interface. The repository contains a React frontend, a set of Spring Boot microservices, Kubernetes deployment manifests, and Stripe-backed subscription flows.

## Overview

Codexa AI combines:

- a project dashboard for managing user workspaces
- a project view with chat, code, and live preview
- an intelligence service that generates and edits code
- a workspace service that stores files, manages previews, and handles project membership
- an account service that manages authentication, users, subscriptions, and Stripe billing
- Kubernetes infrastructure for preview runners, ingress, proxy routing, and shared service configuration

## Repository Structure

```text
Codexa-AI/
├── Frontend/                  React + Vite frontend
├── Microservices/
│   ├── account-service/       Auth, plans, subscriptions, Stripe billing
│   ├── api-gateway/           Entry gateway for service routing
│   ├── common-library/        Shared DTOs, enums, security, errors
│   ├── config-service/        Centralized config server
│   ├── discovery-service/     Eureka service discovery
│   ├── intelligence-service/  AI orchestration and code-generation flows
│   ├── workspace-service/     Project files, members, deploy/preview management
│   └── k8s/                   Kubernetes manifests, ingress, proxy, namespaces
└── README.md
```

## Core Capabilities

### Product features

- AI chat that edits project files in real time
- file tree browsing and code viewing inside each project
- project sharing with owner, editor, and viewer roles
- live preview and publish flows
- Stripe-based subscription upgrades for Free, Plus, and Pro plans
- dashboard-level and project-level billing actions
- global light and dark theme support

### Engineering features

- microservice architecture with shared common library
- JWT-based authentication and authorization
- project preview routing through Redis-backed wildcard proxying
- Kubernetes-based preview runner pool
- per-project preview state persistence on the frontend
- runtime preview error capture with guided AI repair flow

## Frontend

The frontend lives in [Frontend](./Frontend) and is built with:

- React
- TypeScript
- Vite
- Tailwind CSS
- shadcn/ui
- TanStack Query

Main user-facing pages:

- [Index.tsx](./Frontend/src/pages/Index.tsx)
- [ProjectsDashboard.tsx](./Frontend/src/pages/ProjectsDashboard.tsx)
- [ProjectView.tsx](./Frontend/src/pages/ProjectView.tsx)
- [Signup.tsx](./Frontend/src/pages/Signup.tsx)

Important frontend behaviors already implemented in this repo:

- project dashboard search and creation
- attached billing popover on the dashboard plan badge
- project header upgrade flow
- publish action that deploys and opens the preview in a new tab
- per-project preview URL persistence
- success landing page for Stripe checkout at [success.html](./Frontend/public/success.html)

## Backend Services

### Account service

Handles:

- login and signup
- current subscription lookup
- Stripe checkout session creation
- Stripe customer portal access
- webhook processing for subscription lifecycle events

Relevant code:

- [BillingController.java](./Microservices/account-service/src/main/java/com/microservice/codexa/ai/account_service/controller/BillingController.java)
- [StripePaymentProcessor.java](./Microservices/account-service/src/main/java/com/microservice/codexa/ai/account_service/service/impl/StripePaymentProcessor.java)

### Workspace service

Handles:

- project CRUD
- project member management
- file tree and file content retrieval
- project deployment and preview routing
- template initialization for new projects

Relevant code:

- [ProjectController.java](./Microservices/workspace-service/src/main/java/com/microservice/codexa/ai/workspace_service/controller/ProjectController.java)
- [FileController.java](./Microservices/workspace-service/src/main/java/com/microservice/codexa/ai/workspace_service/controller/FileController.java)
- [KubernetesDeploymentServiceImpl.java](./Microservices/workspace-service/src/main/java/com/microservice/codexa/ai/workspace_service/service/impl/KubernetesDeploymentServiceImpl.java)

### Intelligence service

Handles:

- AI-assisted project generation
- prompt construction
- file-tree-aware code generation context
- streaming chat responses and file update events

Relevant code:

- [PromptUtils.java](./Microservices/intelligence-service/src/main/java/com/microservice/codexa/ai/intelligence_service/llm/PromptUtils.java)

## Billing and Subscription Flow

The frontend currently integrates with these account endpoints:

- `GET /api/v1/account/subscription`
- `POST /api/v1/account/payments/checkout`
- `POST /api/v1/account/payments/portal`

Current frontend billing behavior:

- the dashboard shows the active plan badge
- free users can open an upgrade popover and start checkout
- paid users can view plan details and open the Stripe billing portal
- the project view `Upgrade` button mirrors the same flow
- successful Stripe checkout returns to `/success.html`

Plan IDs currently used in the frontend:

- `Codexa Pro` -> `1`
- `Codexa Plus` -> `2`

## Live Preview Architecture

Live preview is not just a frontend iframe. The full flow is:

1. user clicks `Run Preview` or `Publish`
2. frontend calls the workspace deploy endpoint
3. workspace service claims or reuses a preview runner pod
4. project files are mirrored into the runner
5. Vite dev server starts inside the runner
6. Redis route is registered for the project preview host
7. wildcard proxy forwards requests to the active preview target
8. frontend iframe loads the returned preview URL

Important implementation notes:

- preview URLs are now stored per project on the frontend instead of using one global key
- deploy now waits for the preview server to become reachable before returning success
- reused preview pods are health-checked before being routed again

Relevant infrastructure files:

- [Microservices/k8s/proxy/index.js](./Microservices/k8s/proxy/index.js)
- [Microservices/k8s/infra/ingress.yaml](./Microservices/k8s/infra/ingress.yaml)
- [Microservices/k8s/services/workspace-service.yaml](./Microservices/k8s/services/workspace-service.yaml)

## Local Development

### Frontend

```bash
cd Frontend
npm install
npm run dev
```

Example `.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_UNSPLASH_ACCESS_KEY=YOUR_ACCESS_KEY
```

### Microservices

Each backend service is a separate Spring Boot application with its own Maven module:

- `Microservices/account-service`
- `Microservices/api-gateway`
- `Microservices/common-library`
- `Microservices/config-service`
- `Microservices/discovery-service`
- `Microservices/intelligence-service`
- `Microservices/workspace-service`

Typical command:

```bash
cd Microservices/account-service
./mvnw spring-boot:run
```

You will also need the supporting infrastructure configured for your chosen environment:

- PostgreSQL
- Redis
- MinIO
- Stripe keys and webhook secret
- Config service or equivalent env-based configuration

## Kubernetes Deployment

The Kubernetes manifests live under [Microservices/k8s](./Microservices/k8s).

Key pieces:

- shared config map in [infra/namespaces.yaml](./Microservices/k8s/infra/namespaces.yaml)
- frontend deployment in [services/frontend.yaml](./Microservices/k8s/services/frontend.yaml)
- account service deployment in [services/account-service.yaml](./Microservices/k8s/services/account-service.yaml)
- workspace service deployment in [services/workspace-service.yaml](./Microservices/k8s/services/workspace-service.yaml)
- wildcard preview ingress in [infra/ingress.yaml](./Microservices/k8s/infra/ingress.yaml)

Important shared variables already present in the repo:

- `APP_FRONTEND_URL`
- `PREVIEW_DOMAIN`
- `PREVIEW_NAMESPACE`
- `PROXY_PORT`

## API Summary

### Authentication

- `POST /api/v1/account/auth/login`
- `POST /api/v1/account/auth/signup`

### Billing

- `GET /api/v1/account/subscription`
- `POST /api/v1/account/payments/checkout`
- `POST /api/v1/account/payments/portal`
- `POST /api/v1/account/webhooks/payment`

### Projects

- `GET /api/v1/workspace/projects`
- `POST /api/v1/workspace/projects`
- `GET /api/v1/workspace/projects/{id}`
- `PATCH /api/v1/workspace/projects/{id}`
- `DELETE /api/v1/workspace/projects/{id}`
- `POST /api/v1/workspace/projects/{id}/deploy`

### Project files

- `GET /api/v1/workspace/projects/{projectId}/files`
- `GET /api/v1/workspace/projects/{projectId}/files/content?path=...`

### Project members

- `GET /api/v1/workspace/projects/{projectId}/members`
- `POST /api/v1/workspace/projects/{projectId}/members`
- `PATCH /api/v1/workspace/projects/{projectId}/members/{userId}`
- `DELETE /api/v1/workspace/projects/{projectId}/members/{userId}`

## Operational Notes

- frontend auth errors are normalized into readable user-facing messages instead of showing raw backend JSON
- preview startup now shows a waiting state rather than a blank area
- Stripe success page is static and redirects to `/projects`
- account-service billing success URL depends on `APP_FRONTEND_URL` being configured correctly

## Known Risks and Practical Caveats

- preview startup still depends on `npm install` inside runner pods, which can make cold starts slower
- backend preview readiness was improved, but production reliability still depends on runner pool health and Redis route freshness
- chunk size warnings are still present in the frontend build
- local backend verification may require additional permissions or installed infrastructure

## Additional Documentation

- frontend-specific notes: [Frontend/README.md](./Frontend/README.md)
- Kubernetes manifests: [Microservices/k8s](./Microservices/k8s)

## License

No explicit license file is present in this repository. Add one before distributing the project externally.
