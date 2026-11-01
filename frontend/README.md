# LeaveMate Frontend

React, TypeScript, and Vite frontend for the leave-management application.

## Requirements

- Node.js and npm
- Backend running on `http://localhost:8080`

## First-time setup

From this directory:

```bash
npm install
```

The backend URL is configured in `.env`:

```env
VITE_BACKEND_URL=http://localhost:8080
```

Change that value if the backend runs on another host or port. Restart Vite after changing `.env`.

## Run in development

```bash
cd LeaveManagementSystem/frontend
npm run dev
```

Open the URL printed by Vite, normally:

```text
http://localhost:5173
```

## Useful commands

```bash
npm run lint       # Check frontend code
npm run build      # Type-check and create production build
npm run preview    # Preview the production build
```

## Troubleshooting

### `npm start` does not work

This project uses Vite. Use `npm run dev`, not `npm start`.

### `Failed to fetch`

1. Make sure the backend is running on the URL in `.env`.
2. Check that `VITE_BACKEND_URL` does not have a trailing path.
3. Restart the Vite process after changing `.env`.
4. Open the browser developer console for the actual network error.

### Port 5173 is already in use

Vite will usually choose another available port and print it in the terminal. Open that printed URL.

