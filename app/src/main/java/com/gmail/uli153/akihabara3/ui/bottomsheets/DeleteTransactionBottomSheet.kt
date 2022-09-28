package com.gmail.uli153.akihabara3.ui.bottomsheets

import androidx.fragment.app.FragmentManager
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.domain.models.Transaction
import com.gmail.uli153.akihabara3.ui.bottomsheets.base.DeleteBaseBottomSheet

class DeleteTransactionBottomSheet private constructor(
    private val transaction: Transaction,
    listener: DeleteListener<Transaction>
): DeleteBaseBottomSheet<Transaction>(transaction, listener) {

    companion object {
        fun show(manager: FragmentManager, transaction: Transaction, listener: DeleteListener<Transaction>) {
            DeleteTransactionBottomSheet(transaction, listener).show(manager, "DeleteTransactionBottomSheet")
        }
    }

    override val message: String get() {
        return getString(R.string.want_to_remove_transaction, transaction.title)
    }
}
