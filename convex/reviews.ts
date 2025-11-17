import { mutation, query } from "./_generated/server";
import { v } from "convex/values";
import { currentUser } from "./users";

/**
 * Mutation to add or update a review for a movie by the current user.
 */
export const addOrUpdateReview = mutation({
  args: {
    movieId: v.number(),
    rating: v.number(),
    comment: v.string(),
  },
  handler: async (ctx, args) => {
    const user = await currentUser(ctx, {});
    if (!user) return;

    // Check if a review already exists for this user and movie
    const existingReview = await ctx.db
      .query("reviews")
      .withIndex("by_user_movie", (q) =>
        q.eq("userId", user._id).eq("movieId", args.movieId)
      )
      .unique();

    if (existingReview) {
      // Update the existing review
      await ctx.db.patch(existingReview._id, {
        rating: args.rating,
        comment: args.comment,
      });
    } else {
      // Insert a new review
      await ctx.db.insert("reviews", {
        userId: user._id,
        movieId: args.movieId,
        author: user.name,
        rating: args.rating,
        comment: args.comment,
      });
    }
  },
});

/**
 * Query to get all reviews for a specific movie.
 */
export const getMovieReviews = query({
  args: { movieId: v.number() },
  handler: async (ctx, args) => {
    return ctx.db
      .query("reviews")
      .withIndex("by_movie", (q) => q.eq("movieId", args.movieId))
      .collect();
  },
});

/**
 * Query to get all reviews written by the current user.
 */
export const getUserReviews = query({
    handler: async (ctx) => {
      const user = await currentUser(ctx, {});
      if (!user) {
        return [];
      }
      return ctx.db
        .query("reviews")
        .filter((q) => q.eq(q.field("userId"), user._id))
        .collect();
    },
  });
