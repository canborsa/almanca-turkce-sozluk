import { mutation, query } from "./_generated/server";
import { v } from "convex/values";

/**
 * Gets the user record for the current authenticated user.
 * Throws an error if the user is not authenticated.
 */
export const currentUser = query({
  handler: async (ctx) => {
    const identity = await ctx.auth.getUserIdentity();
    if (!identity) {
      throw new Error("Not authenticated");
    }
    const user = await ctx.db
      .query("users")
      .withIndex("by_token", (q) =>
        q.eq("tokenIdentifier", identity.tokenIdentifier)
      )
      .unique();

    if (!user) {
      throw new Error("User not found.");
    }
    return user;
  },
});

/**
 * Creates a new user or returns the existing user for the current session.
 */
export const getOrCreateUser = mutation({
  handler: async (ctx) => {
    const identity = await ctx.auth.getUserIdentity();
    if (!identity) {
      throw new Error("Called getOrCreateUser without authentication present");
    }

    // Check if user already exists
    const user = await ctx.db
      .query("users")
      .withIndex("by_token", (q) =>
        q.eq("tokenIdentifier", identity.tokenIdentifier)
      )
      .unique();

    if (user !== null) {
      return user._id;
    }

    // If it's a new user, create it
    const userId = await ctx.db.insert("users", {
      name: identity.name!,
      email: identity.email!,
      profileImageUrl: identity.profileUrl,
      tokenIdentifier: identity.tokenIdentifier,
    });

    return userId;
  },
});
