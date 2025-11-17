import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { Id } from "./_generated/dataModel";

export const addOrUpdateReview = mutation({
  args: {
    userId: v.id("users"),
    movieId: v.number(),
    rating: v.number(),
    comment: v.string(),
  },
  handler: async (ctx, args) => {
    // We need the user's name, so we fetch the user document.
    const user = await ctx.db.get(args.userId);
    if (!user) return;

    const existingReview = await ctx.db
      .query("reviews")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", args.userId).eq("movieId", args.movieId)
      )
      .unique();

    if (existingReview) {
      await ctx.db.patch(existingReview._id, {
        rating: args.rating,
        comment: args.comment,
      });
    } else {
      await ctx.db.insert("reviews", {
        userId: args.userId,
        movieId: args.movieId,
        author: user.name, // Get name from user doc
        rating: args.rating,
        comment: args.comment,
      });
    }
  },
});

export const getMovieReviews = query({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    return ctx.db
      .query("reviews")
      .withIndex("by_movie", (q) => q.eq("movieId", args.movieId))
      .collect();
  },
});

export const getUserReviews = query({
  args: { userId: v.id("users") },
  handler: async (ctx, args) => {
    return ctx.db
      .query("reviews")
      .filter((q) => q.eq(q.field("userId"), args.userId))
      .collect();
  },
});
