import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { Id } from "./_generated/dataModel";

export const getWatchlist = query({
  args: { userId: v.id("users") },
  handler: async (ctx, args) => {
    return ctx.db
      .query("watchlist")
      .filter((q) => q.eq(q.field("userId"), args.userId))
      .collect();
  },
});

export const isOnWatchlist = query({
  args: { userId: v.id("users"), movieId: v.number() },
  handler: async (ctx, args) => {
    const item = await ctx.db
      .query("watchlist")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", args.userId).eq("movieId", args.movieId)
      )
      .unique();
    return !!item;
  },
});

export const addWatchlist = mutation({
  args: {
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterPath: v.string(),
  },
  handler: async (ctx, args) => {
    await ctx.db.insert("watchlist", args);
  },
});

export const removeWatchlist = mutation({
  args: { userId: v.id("users"), movieId: v.number() },
  handler: async (ctx, args) => {
    const item = await ctx.db
      .query("watchlist")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", args.userId).eq("movieId", args.movieId)
      )
      .unique();
    if (item) {
      await ctx.db.delete(item._id);
    }
  },
});
