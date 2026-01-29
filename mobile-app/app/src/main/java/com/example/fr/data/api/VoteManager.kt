package com.example.fr.data.api

import android.content.Context
import android.content.SharedPreferences

class VoteManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "fr_vote_prefs"
        private const val KEY_VOTED_REPORTS = "voted_reports"
    }

    // Vote types
    enum class VoteType {
        UPVOTE,
        DOWNVOTE
    }

    // Check if user has already voted on a report
    fun hasVoted(reportId: Int): Boolean {
        val votedReports = getVotedReports()
        return votedReports.containsKey(reportId)
    }

    // Get the vote type for a report (null if not voted)
    fun getVoteType(reportId: Int): VoteType? {
        val votedReports = getVotedReports()
        return votedReports[reportId]
    }

    // Record a vote for a report
    fun recordVote(reportId: Int, voteType: VoteType) {
        val votedReports = getVotedReports().toMutableMap()
        votedReports[reportId] = voteType
        saveVotedReports(votedReports)
    }

    // Remove a vote (if you want to allow changing votes)
    fun removeVote(reportId: Int) {
        val votedReports = getVotedReports().toMutableMap()
        votedReports.remove(reportId)
        saveVotedReports(votedReports)
    }

    // Get all voted reports
    private fun getVotedReports(): Map<Int, VoteType> {
        val votedString = prefs.getString(KEY_VOTED_REPORTS, "") ?: ""
        if (votedString.isEmpty()) return emptyMap()

        return try {
            votedString.split(";")
                .filter { it.isNotEmpty() }
                .associate { entry ->
                    val parts = entry.split(":")
                    val reportId = parts[0].toInt()
                    val voteType = if (parts[1] == "U") VoteType.UPVOTE else VoteType.DOWNVOTE
                    reportId to voteType
                }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // Save voted reports
    private fun saveVotedReports(votedReports: Map<Int, VoteType>) {
        val votedString = votedReports.entries.joinToString(";") { (reportId, voteType) ->
            val typeChar = if (voteType == VoteType.UPVOTE) "U" else "D"
            "$reportId:$typeChar"
        }
        prefs.edit().putString(KEY_VOTED_REPORTS, votedString).apply()
    }

    // Clear all votes (e.g., on logout)
    fun clearAllVotes() {
        prefs.edit().remove(KEY_VOTED_REPORTS).apply()
    }
}
