import { defineSchema, defineTable } from "convex/server";
import { v } from "convex/values";

export default defineSchema({
  users: defineTable({
    name: v.string(),
    email: v.string(),
    profileImageUrl: v.optional(v.string()),
    tokenIdentifier: v.string(),
  })
    .index("by_token", ["tokenIdentifier"])
    .index("by_email", ["email"]),

  favorites: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterUrl: v.string(),
  })
    .index("by_user_movie", ["userId", "movieId"]),

  watchlist: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterUrl: v.string(),
  })
    .index("by_user_movie", ["userId", "movieId"]),

  reviews: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    author: v.string(),
    rating: v.number(),
    comment: v.string(),
  })
    .index("by_user_movie", ["userId", "movieId"])
    .index("by_movie", ["movieId"]),
});
