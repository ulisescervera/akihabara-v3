package com.gmail.uli153.akihabara3.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.daimajia.swipe.SwipeLayout
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.databinding.FragmentHistoryBinding
import com.gmail.uli153.akihabara3.databinding.FragmentSettingsBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheet.DeleteTransactionBottomSheet
import com.gmail.uli153.akihabara3.ui.bottomsheet.base.DeleteBaseBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.DataWrapper
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.setTransactionImage
import kotlinx.android.synthetic.main.row_transaction.view.*
import kotlinx.android.synthetic.main.row_transaction.view.label_name
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

interface HistoryListener {
    fun onRollbackTransaction(transaction: Transaction)
}

class HistoryFragment : AkbFragment(), HistoryListener, DeleteBaseBottomSheet.DeleteListener<Transaction> {

    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    protected val productsViewModel: ProductsViewModel by activityViewModels()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class TransactionVH(view: View, listener: HistoryListener): RecyclerView.ViewHolder(view) {

        init {
            itemView.btn_remove.setSafeClickListener {
                listener.onRollbackTransaction(transaction)
            }
            itemView.surface.setSafeClickListener {
                when (itemView.swipe.openStatus) {
                    SwipeLayout.Status.Open -> itemView.swipe.close(true)
                    SwipeLayout.Status.Close -> itemView.swipe.open(true)
                }
            }
        }

        private lateinit var transaction: Transaction
        fun set(transaction: Transaction) {
            this.transaction = transaction

            itemView.image.setTransactionImage(transaction)
            if (transaction.date != null) {
                itemView.label_date.text = dateFormatter.format(transaction.date)
                itemView.label_date.visibility = View.VISIBLE
            } else {
                itemView.label_date.visibility = View.GONE
            }

            itemView.label_name.text = if (transaction.type == TransactionType.BALANCE) {
                getString(R.string.added_balance)
            } else {
                transaction.title
            }

            val amount = if (transaction.type == TransactionType.BUY) -transaction.amount else transaction.amount
            itemView.label_amount.text = AkbNumberParser.LocaleParser.parseToEur(amount)
        }
    }

    private inner class Adapter(
        private val context: Context,
        private val listener: HistoryListener
    ): ListAdapter<Transaction, TransactionVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
            val view = LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false)
            return TransactionVH(view, listener)
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
