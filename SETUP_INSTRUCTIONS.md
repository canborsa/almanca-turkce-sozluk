# TMDB Clone App - Setup Instructions

To run this application, you need to configure both the Convex backend and the Android client. Please follow these steps carefully.

## Step 1: Deploy the Convex Backend

The backend code is located in the `convex/` directory. You need to deploy it to your own Convex account.

1.  **Install the Convex CLI:** If you haven't already, install the Convex command-line tool:
    ```bash
    npm install -g convex
    ```
2.  **Initialize Convex:** Navigate to the project's root directory in your terminal and log in to your Convex account:
    ```bash
    convex login
    ```
3.  **Deploy the Backend:** Deploy the backend code to create your project. This command will push the schema and functions in the `convex/` directory to your account.
    ```bash
    convex deploy
    ```
4.  **Get Your Deployment URL:** After a successful deployment, the CLI will output your project's **Deployment URL**. It will look something like this: `https://happy-animal-123.convex.cloud`. **Copy this URL.** You will need it for the next step.

## Step 2: Configure the Android Client

The Android application needs to know your TMDB API Key and your unique Convex Deployment URL.

1.  **Create `local.properties` file:** In the root directory of the project (the same level as `settings.gradle`), create a new file named `local.properties`.

2.  **Add Your Keys:** Open the `local.properties` file and add the following two lines.
    *   Replace `"YOUR_TMDB_API_KEY"` with your actual API key from The Movie Database.
    *   Replace `"YOUR_CONVEX_URL"` with the URL you copied in the previous step.

    ```properties
    tmdb.apiKey="YOUR_TMDB_API_KEY"
    convex.url="YOUR_CONVEX_URL"
    ```

3.  **Sync Gradle:** Open the project in Android Studio. It should automatically sync. If not, click the "Sync Project with Gradle Files" button (elephant icon).

## Step 3: Run the Application

You can now build and run the application on an emulator or a physical device. The app should be fully functional for testing.

## Step 4: Secure Passwords (IMPORTANT for Production)

The current backend is a **testing prototype** and **DOES NOT store passwords securely**. Before you consider deploying this for real users, you **MUST** implement secure password hashing.

1.  **Install `bcryptjs`:** In your terminal (in the project's root directory), run:
    ```bash
    npm install bcryptjs
    npm install --save-dev @types/bcryptjs
    ```
2.  **Update `convex/users.ts`:** Open the `convex/users.ts` file and follow the `STEP` comments to replace the insecure password functions with the secure `bcryptjs` versions. This involves uncommenting the secure code and deleting the insecure placeholders.

3.  **Re-deploy the Backend:** After updating the file, deploy your backend again to apply the security patch:
    ```bash
    convex deploy
    ```

Your application is now ready and secure.
