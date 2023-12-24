package model

class NoChangesException : IllegalStateException("No changes")
class GameDeletedException : IllegalStateException("Game deleted")