import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { Id } from "./_generated/dataModel";

export const getFavorites = query({
  args: { userId: v.id("users") },
  handler: async (ctx, args) => {
    return ctx.db
      .query("favorites")
      .filter((q) => q.eq(q.field("userId"), args.userId))
      .collect();
  },
});

export const isFavorite = query({
  args: { userId: v.id("users"), movieId: v.number() },
  handler: async (ctx, args) => {
    const favorite = await ctx.db
      .query("favorites")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", args.userId).eq("movieId", args.movieId)
      )
      .unique();
    return !!favorite;
  },
});

export const addFavorite = mutation({
  args: {
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterPath: v.string(),
  },
  handler: async (ctx, args) => {
    await ctx.db.insert("favorites", args);
  },
});

export const removeFavorite = mutation({
  args: { userId: v.id("users"), movieId: v.number() },
  handler: async (ctx, args) => {
    const favorite = await ctx.db
      .query("favorites")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", args.userId).eq("movieId", args.movieId)
      )
      .unique();
    if (favorite) {
      await ctx.db.delete(favorite._id);
    }
  },
});
