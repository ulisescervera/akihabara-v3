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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.databinding.FragmentHistoryBinding
import com.gmail.uli153.akihabara3.databinding.FragmentSettingsBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.DataWrapper
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_transaction.view.*
import kotlinx.android.synthetic.main.row_transaction.view.label_name
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

interface HistoryListener {
    fun onRollbackTransaction(trasaction: Transaction)
}

class HistoryFragment : AkbFragment(), HistoryListener {

    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    protected val productsViewModel: ProductsViewModel by activityViewModels()

    private val transactions: MutableList<Transaction> = mutableListOf()

    private val adapter by lazy {
        Adapter(transactions, requireContext(), this)
    }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
    private val numberFormatter = DecimalFormat("0.00")

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
//        touchHelper.attachToRecyclerView(binding.recyclerviewProducts)
        binding.recyclerviewTransactions.adapter = adapter

        productsViewModel.transactions.observe(viewLifecycleOwner) {
            transactions.clear()
            if (it is DataWrapper.Success<List<Transaction>>) {
                transactions.addAll(it.data)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRollbackTransaction(trasaction: Transaction) {
        //todo
    }

    private inner class TransactionVH(view: View, listener: HistoryListener): RecyclerView.ViewHolder(view) {

        init {
            itemView.btn_remove.setSafeClickListener {
                listener.onRollbackTransaction(transaction)
            }
        }

        private lateinit var transaction: Transaction
        fun set(transaction: Transaction) {
            this.transaction = transaction

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

            itemView.label_amount.text = numberFormatter.format(transaction.amount)
        }
    }

    private inner class Adapter(
        private val items: List<Transaction>,
        private val context: Context,
        private val listener: HistoryListener
    ): RecyclerView.Adapter<TransactionVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
            val view = LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false)
            return TransactionVH(view, listener)
        }

        override fun onBindViewHolder(holder: TransactionVH, position: Int) {
            holder.set(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }
}