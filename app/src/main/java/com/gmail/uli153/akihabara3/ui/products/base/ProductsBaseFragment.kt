package com.gmail.uli153.akihabara3.ui.products

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBaseBinding
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*

interface ProductListener {
    fun onBuyProduct(product: Product)
    fun onFavoriteProduct(product: Product)
    fun onEditProduct(product: Product)
}

abstract class ProductsBaseFragment : AkbFragment(), ProductListener {

    private val SNACKBAR_MAX_DURATION = 4000 // ms

    private var _binding: FragmentProductsBaseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    protected val productsViewModel: ProductsViewModel by activityViewModels()

    protected var products: List<Product> = listOf()
        set(value) {
            field = value
            filter()
        }
    private var filteredProducts: List<Product> = listOf()
        set(value) {
            field = value
            adapter.submitList(value)
        }
    protected val adapter: ProductAdapter by lazy {
        ProductAdapter(this)
    }

    private val snackQueue: Queue<Snackbar> = LinkedList()
    private var currentSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductsBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val separator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.separator_product)!!)
        }
        binding.recyclerviewProducts.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerviewProducts.addItemDecoration(separator)
        binding.recyclerviewProducts.adapter = adapter
    }

    override fun onBuyProduct(product: Product) {
        productsViewModel.buyProduct(product) { transactionId ->
            showUndoSnackbar(product) {
                productsViewModel.deleteTransaction(transactionId)
            }
        }
    }

    override fun onFavoriteProduct(product: Product) {
        productsViewModel.toggleFavorite(product)
    }

    override fun onEditProduct(product: Product) {
        val dirs = ProductsFragmentDirections.actionEditProduct(product.id)
        navigate(dirs)
    }

    protected fun filter() {
        //todo filter
        filteredProducts = products
    }

    private fun showUndoSnackbar(product: Product, listener: () -> Unit) {
        var snackbar: Snackbar? = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.snack_bar_undo, null)
        val label = view.findViewById<TextView>(R.id.label_message)
        val button = view.findViewById<Button>(R.id.btn_undo)
        val progressBar = view.findViewById<LinearProgressIndicator>(R.id.progress_bar)

        progressBar.isIndeterminate = false
        progressBar.max = SNACKBAR_MAX_DURATION
        label.text = getString(R.string.snackbar_undo_message, product.name, AkbNumberParser.LocaleParser.format(product.price))
        button.setSafeClickListener {
            snackbar?.dismiss()
            listener()
        }

        val job = lifecycleScope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
            while (progressBar.progress > 0) {
                delay(10)
                progressBar.progress -= 10
            }
            snackbar?.dismiss()
        }

        snackbar?.apply {
//            duration = SNACKBAR_MAX_DURATION // hay desfase entre la coroutina y el snackbar
            val rootView = getView() as Snackbar.SnackbarLayout
            val layoutParams = getView().layoutParams as? CoordinatorLayout.LayoutParams
            if (layoutParams != null) {
                getView().layoutParams = layoutParams.apply {
                    gravity = Gravity.TOP
                }
            }
            rootView.setPadding(0, 0, 0, 0)
            rootView.addView(view, 0)
            addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    job.cancel()
                    snackQueue.remove(snackbar)
                    currentSnackbar = null
                    snackQueue.peek()?.show()
                    snackbar = null
                }
                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                    currentSnackbar = snackbar
                    progressBar.progress = SNACKBAR_MAX_DURATION
                    job.start()
                }
            })
        }

        if (snackQueue.size > 0) {
            if (currentSnackbar == null) {
                snackQueue.clear()
                snackQueue.add(snackbar!!)
                snackbar?.show()
            } else {
                snackQueue.add(snackbar!!)
            }
        } else {
            snackQueue.add(snackbar!!)
            snackbar?.show()
        }
    }

    protected inner class ProductVH(
        view: View,
        private val context: Context,
        private val listener: ProductListener
    ) : RecyclerView.ViewHolder(view) {

        private var toggleFavoriteJob: Job? = null

        init {
            itemView.btn_buy.setSafeClickListener {
                itemView.swipe.close(true)
                listener.onBuyProduct(product)
            }
            itemView.btn_favorite.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    onToggleLike(product, true)
                }
                override fun unLiked(likeButton: LikeButton?) {
                    onToggleLike(product, false)
                }
            })
            itemView.btn_favorite.setOnAnimationEndListener {
                // Solo cuando like se pone a true se lanza este callback
                toggleFavoriteJob?.start()
            }
            itemView.btn_edit.setSafeClickListener {
                listener.onEditProduct(product)
            }
        }

        lateinit var product: Product private set
        fun setup(product: Product) {
            this.product = product
            itemView.swipe.dragDistance = 120
            itemView.swipe.close(false)
            itemView.label_name.text = product.name
            itemView.label_price.text = String.format("%s €", AkbNumberParser.LocaleParser.format(product.price))
            itemView.btn_favorite.isLiked = product.favorite
        }

        private fun onToggleLike(product: Product, lazy: Boolean) {
            val start = if (lazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
            toggleFavoriteJob = lifecycleScope.launch(Dispatchers.Main, start = start) {
                if (!lazy) delay(100)
                listener.onFavoriteProduct(product)
            }
        }
    }

    protected inner class ProductDiffCallback: DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    protected inner class ProductAdapter(
        private val listener: ProductListener
    ) : ListAdapter<Product, ProductVH>(ProductDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.row_product, parent, false)
            return ProductVH(view, requireContext(), listener)
        }

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.setup(getItem(position))
        }
    }
}
