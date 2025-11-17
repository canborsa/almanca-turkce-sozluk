import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { currentUser } from "./users";

/**
 * Query to check if a movie is in the current user's watchlist.
 */
export const isInWatchlist = query({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return false;

    const watchlistItem = await ctx.db
      .query("watchlist")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", user._id).eq("movieId", args.movieId)
      )
      .unique();

    return watchlistItem !== null;
  },
});

/**
 * Mutation to add a movie to the current user's watchlist.
 */
export const addToWatchlist = mutation({
  args: { movieId: v.number(), title: v.string(), posterUrl: v.string() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return;

    const existing = await isInWatchlist(ctx, { movieId: args.movieId });
    if (existing) return;

    await ctx.db.insert("watchlist", {
      userId: user._id,
      movieId: args.movieId,
      title: args.title,
      posterUrl: args.posterUrl,
    });
  },
});

/**
 * Mutation to remove a movie from the current user's watchlist.
 */
export const removeFromWatchlist = mutation({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return;

    const watchlistItem = await ctx.db
      .query("watchlist")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", user._id).eq("movieId", args.movieId)
      )
      .unique();

    if (watchlistItem) {
      await ctx.db.delete(watchlistItem._id);
    }
  },
});

/**
 * Query to get all watchlist movies for the current user.
 */
export const getWatchlist = query({
  handler: async (ctx) => {
    const user = await currentUser(ctx, {});
    if (!user) {
      return [];
    }
    return ctx.db
      .query("watchlist")
      .filter((q) => q.eq(q.field("userId"), user._id))
      .collect();
  },
});
