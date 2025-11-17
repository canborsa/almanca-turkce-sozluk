import { defineSchema, defineTable } from "convex/server";
import { v } from "convex/values";

export default defineSchema({
  users: defineTable({
    name: v.string(),
    email: v.string(),
    tokenIdentifier: v.string(),
  }).index("by_token", ["tokenIdentifier"])
    .index("by_email", ["email"]), // Index for fast email lookups

  reviews: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    author: v.string(),
    rating: v.number(),
    comment: v.string(),
  }).index("by_movie", ["movieId"])
    .index("by_user_movie", ["userId", "movieId"]),

  favorites: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterPath: v.string(),
  }).index("by_user_movie", ["userId", "movieId"]),

  watchlist: defineTable({
    userId: v.id("users"),
    movieId: v.number(),
    title: v.string(),
    posterPath: v.string(),
  }).index("by_user_movie", ["userId", "movieId"]),
});
