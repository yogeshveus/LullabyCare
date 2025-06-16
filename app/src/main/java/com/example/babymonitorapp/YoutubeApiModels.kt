package com.example.babymonitorapp

data class PlaylistItemsResponse(
    val items: List<PlaylistItemDetail>
    // Add nextPageToken if you want to implement pagination
    // val nextPageToken: String?
)

data class PlaylistItemDetail(
    val snippet: Snippet
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val resourceId: ResourceId,
    val publishedAt: String // Often useful
)

data class Thumbnails(
    val default: Thumbnail?, // Optional: provide fallbacks
    val medium: Thumbnail?,
    val high: Thumbnail?
)

data class Thumbnail(
    val url: String,
    val width: Int?,
    val height: Int?
)

data class ResourceId(
    val videoId: String
)