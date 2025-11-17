# Convex Backend Security Instructions

This backend is currently configured for **prototyping and testing only**. Before deploying this application to a production environment, you **MUST** implement secure password handling. Storing passwords in plaintext or a reversible format is a critical security vulnerability.

## How to Secure Passwords

We recommend using `bcryptjs`, a secure and widely-used password hashing library.

### Step 1: Install bcryptjs

Open your terminal in the root directory of this project and run the following command to add `bcryptjs` to your Convex project:

```bash
npm install bcryptjs
```
You will also need the type definitions for it:
```bash
npm install --save-dev @types/bcryptjs
```

### Step 2: Update `convex/users.ts`

Open the `convex/users.ts` file and follow the instructions within the comments. You will need to:

1.  **Uncomment** the `import bcrypt from "bcryptjs";` line at the top of the file.
2.  **Replace** the body of the `securePasswordHash` function with the provided `bcrypt.hash` implementation.
3.  **Replace** the body of the `securePasswordCompare` function with the provided `bcrypt.compare` implementation.
4.  **Delete** the old, insecure `insecure...` functions.

By following these steps, you will ensure that user passwords are securely hashed and stored, protecting your users' data.
