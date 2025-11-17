import {
  internalMutation,
  httpAction,
  query
} from "./_generated/server";
import { internal } from "./_generated/api";
import { v } from "convex/values";
import { Doc, Id } from "./_generated/dataModel";
// STEP 1: Uncomment this line after running `npm install bcryptjs @types/bcryptjs`
// import bcrypt from "bcryptjs";

// --- INSECURE PROTOTYPE FUNCTIONS (DELETE THESE IN PRODUCTION) ---
// WARNING: This is for prototyping ONLY. It does NOT securely hash passwords.
const insecurePasswordHash = (password: string) => `hashed_${password}`;
const insecurePasswordCompare = (password: string, hash: string) => `hashed_${password}` === hash;
// --- END INSECURE FUNCTIONS ---


// --- SECURE PRODUCTION FUNCTIONS (USE THESE IN PRODUCTION) ---
/*
// STEP 2: Replace the insecure functions above with these secure ones.
const securePasswordHash = async (password: string) => {
  return await bcrypt.hash(password, 10);
};
const securePasswordCompare = async (password: string, hash: string) => {
  return await bcrypt.compare(password, hash);
};
*/
// --- END SECURE FUNCTIONS ---


export const getUser = query(
  async (ctx, { id }: { id: Id<"users"> }): Promise<Doc<"users"> | null> => {
    return await ctx.db.get(id);
  }
);

export const internal_createUser = internalMutation({
  args: { email: v.string(), passwordHash: v.string(), name: v.string() },
  handler: async (ctx, { email, passwordHash, name }) => {
    const existingUser = await ctx.db.query("users").withIndex("by_email", (q) => q.eq("email", email)).unique();
    if(existingUser){ throw new Error("User with this email already exists."); }
    return await ctx.db.insert("users", { email, name, tokenIdentifier: passwordHash });
  },
});

export const handleSignUp = httpAction(async (ctx, request) => {
  const { email, password, name } = await request.json();
  if (!email || !password || !name) { return new Response("Missing required fields", { status: 400 }); }
  try {
    // STEP 3: Replace `insecurePasswordHash` with `securePasswordHash`
    const passwordHash = insecurePasswordHash(password);
    const userId = await ctx.runMutation(internal.users.internal_createUser, { email, passwordHash, name });
    return new Response(JSON.stringify({ userId: userId.toString() }), { status: 200 });
  } catch (e) {
    const error = e as Error;
    if (error.message.includes("already exists")) {
      return new Response("User with this email already exists.", { status: 409 });
    }
    return new Response(`Failed to create user: ${error.message}`, { status: 500 });
  }
});


export const internal_logIn = internalMutation({
  args: { email: v.string(), password: v.string() },
  handler: async (ctx, { email, password }) => {
    const user = await ctx.db.query("users").withIndex("by_email", (q) => q.eq("email", email)).unique();
    if (!user) { throw new Error("User not found"); }
    // STEP 4: Replace `insecurePasswordCompare` with `securePasswordCompare`
    const isCorrect = insecurePasswordCompare(password, user.tokenIdentifier);
    if (!isCorrect) { throw new Error("Incorrect password."); }
    return user._id;
  },
});

export const handleLogIn = httpAction(async (ctx, request) => {
  const { email, password } = await request.json();
  if (!email || !password) { return new Response("Missing required fields", { status: 400 }); }
  try {
    const userId = await ctx.runMutation(internal.users.internal_logIn, { email, password });
    return new Response(JSON.stringify({ userId: userId.toString() }), { status: 200 });
  } catch (e) {
    return new Response(`Invalid credentials: ${(e as Error).message}`, { status: 401 });
  }
});
