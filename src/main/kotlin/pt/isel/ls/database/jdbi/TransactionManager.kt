package pt.isel.ls.database.jdbi
interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
