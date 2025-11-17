import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { currentUser } from "./users";

/**
 * Query to check if a movie is in the current user's favorites.
 */
export const isFavorite = query({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return false;

    const favorite = await ctx.db
      .query("favorites")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", user._id).eq("movieId", args.movieId)
      )
      .unique();

    return favorite !== null;
  },
});

/**
 * Mutation to add a movie to the current user's favorites.
 */
export const addFavorite = mutation({
  args: { movieId: v.number(), title: v.string(), posterUrl: v.string() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return;

    // Prevent adding duplicates
    const existing = await isFavorite(ctx, { movieId: args.movieId });
    if (existing) return;

    await ctx.db.insert("favorites", {
      userId: user._id,
      movieId: args.movieId,
      title: args.title,
      posterUrl: args.posterUrl,
    });
  },
});

/**
 * Mutation to remove a movie from the current user's favorites.
 */
export const removeFavorite = mutation({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return;

    const favorite = await ctx.db
      .query("favorites")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", user._id).eq("movieId", args.movieId)
      )
      .unique();

    if (favorite) {
      await ctx.db.delete(favorite._id);
    }
  },
});

/**
 * Query to get all favorite movies for the current user.
 */
export const getFavorites = query({
  handler: async (ctx) => {
    const user = await currentUser(ctx, {});
    if (!user) {
      return [];
    }
    return ctx.db
      .query("favorites")
      .filter((q) => q.eq(q.field("userId"), user._id))
      .collect();
  },
});
