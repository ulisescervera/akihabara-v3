package com.gmail.uli153.akihabara3.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.*
import com.daimajia.swipe.SwipeLayout
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.data.entities.TransactionType
import com.gmail.uli153.akihabara3.databinding.FragmentHistoryBinding
import com.gmail.uli153.akihabara3.domain.models.Transaction
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheets.DeleteTransactionBottomSheet
import com.gmail.uli153.akihabara3.ui.bottomsheets.base.DeleteBaseBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.databinding.FragmentCropBinding
import com.gmail.uli153.akihabara3.databinding.RowTransactionBinding
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.extensions.setTransactionImage
import java.text.SimpleDateFormat
import java.util.*

interface HistoryListener {
    fun onRollbackTransaction(transaction: Transaction)
}

class HistoryFragment : AkbFragment<FragmentHistoryBinding>(), HistoryListener, DeleteBaseBottomSheet.DeleteListener<Transaction> {

    protected val productsViewModel: ProductsViewModel by activityViewModels()

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }

    private val adapter by lazy {
        Adapter(requireContext(), this).apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.recyclerviewTransactions.smoothScrollToPosition(positionStart)
                }
            })
        }
    }

    private val dateFormatter = SimpleDateFormat("E dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onRollbackTransaction(transaction: Transaction) {
        DeleteTransactionBottomSheet.show(childFragmentManager, transaction, this)
    }

    override fun onDeleteItem(item: Transaction) {
        productsViewModel.deleteTransaction(item.id)
    }

    override fun onCancel(item: Transaction) {
        adapter.currentList.indexOfFirst { it.id == item.id }.takeIf { it >= 0 }?.let {
            adapter.notifyItemChanged(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val separator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.separator_product)!!)
        }
        binding.recyclerviewTransactions.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerviewTransactions.addItemDecoration(separator)
        binding.recyclerviewTransactions.adapter = adapter

        productsViewModel.transactions.observe(viewLifecycleOwner) {
            val data = if (it is DataWrapper.Success<List<Transaction>>) {
                it.data
            } else emptyList()
            adapter.submitList(data)
        }
    }

    private inner class TransactionVH(
        private val binding: RowTransactionBinding,
        listener: HistoryListener
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRemove.setSafeClickListener {
                listener.onRollbackTransaction(transaction)
            }
            binding.surface.setSafeClickListener {
                when (binding.swipe.openStatus) {
                    SwipeLayout.Status.Open -> binding.swipe.close(true)
                    SwipeLayout.Status.Close -> binding.swipe.open(true)
                }
            }
        }

        private lateinit var transaction: Transaction
        fun set(transaction: Transaction) {
            this.transaction = transaction

            binding.image.setTransactionImage(transaction)

            binding.labelDate.text = dateFormatter.format(transaction.date)
            binding.labelDate.visibility = View.VISIBLE

            binding.labelName.text = if (transaction.type == TransactionType.BALANCE) {
                getString(R.string.added_balance)
            } else {
                transaction.title
            }

            val amount = if (transaction.type == TransactionType.BUY) -transaction.amount else transaction.amount
            binding.labelAmount.text = AkbNumberParser.LocaleParser.parseToEur(amount)
        }
    }

    private inner class Adapter(
        private val context: Context,
        private val listener: HistoryListener
    ): ListAdapter<Transaction, TransactionVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
            return TransactionVH(RowTransactionBinding.inflate(LayoutInflater.from(context), parent, false), listener)
        }

        override fun onBindViewHolder(holder: TransactionVH, position: Int) {
            holder.set(getItem(position))
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
