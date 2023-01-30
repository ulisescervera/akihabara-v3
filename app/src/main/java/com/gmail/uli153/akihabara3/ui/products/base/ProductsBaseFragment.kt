package com.gmail.uli153.akihabara3.ui.products.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.daimajia.swipe.SwipeLayout
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBaseBinding
import com.gmail.uli153.akihabara3.databinding.RowProductBinding
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.products.ProductsFragmentDirections
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.SnackBarManager
import com.gmail.uli153.akihabara3.utils.extensions.setProductImage
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.coroutines.*

interface ProductListener {
    fun onBuyProduct(Product: Product)
    fun onFavoriteProduct(Product: Product)
    fun onEditProduct(Product: Product)
}

abstract class ProductsBaseFragment : AkbFragment<FragmentProductsBaseBinding>() {

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentProductsBaseBinding {
        return FragmentProductsBaseBinding.inflate(inflater, container, false)
    }

    protected val productsViewModel: ProductsViewModel by activityViewModels()

    protected var products: List<Product> = listOf()
        set(value) {
            field = value
            filter()
        }

    private var filteredProductEntities: List<Product> = listOf()
        set(value) {
            field = value
            adapter.submitList(value)
        }

    protected val adapter: ProductAdapter by lazy {
        val listener = object: ProductListener {
            override fun onBuyProduct(Product: Product) {
                productsViewModel.buyProduct(Product) { transactionId ->
                    val message = getString(R.string.snackbar_undo_message, Product.name, AkbNumberParser.LocaleParser.format(Product.price))
                    snackBarManager?.showUndoSnackbar(message) {
                        productsViewModel.deleteTransaction(transactionId)
                    }
                }
            }

            override fun onFavoriteProduct(Product: Product) {
                productsViewModel.toggleFavorite(Product)
            }

            override fun onEditProduct(Product: Product) {
                val dirs = ProductsFragmentDirections.actionEditProduct(Product.id)
                navigate(dirs)
            }
        }
        val adapter = ProductAdapter(listener)
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                smoothScroller.targetPosition = Math.max(0, positionStart - 1)
                layoutManager.startSmoothScroll(smoothScroller)
            }
        })
        adapter
    }

    protected lateinit var layoutManager: LinearLayoutManager
    protected lateinit var smoothScroller: LinearSmoothScroller
    private var snackBarManager: SnackBarManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        snackBarManager = SnackBarManager(requireContext(), binding.root, lifecycleScope)
        smoothScroller = object: LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        val separator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.separator_product)!!)
        }
        binding.recyclerviewProducts.layoutManager = layoutManager
        binding.recyclerviewProducts.addItemDecoration(separator)
        binding.recyclerviewProducts.adapter = adapter
    }

    override fun onDestroyView() {
        binding.recyclerviewProducts.adapter = null
        binding.recyclerviewProducts.layoutManager = null
        snackBarManager = null
        super.onDestroyView()
    }
    protected fun filter() {
        //todo filter
        filteredProductEntities = products
    }

    protected inner class ProductVH(
        private val binding: RowProductBinding,
        private val listener: ProductListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var toggleFavoriteJob: Job? = null

        init {
            binding.btnBuy.setSafeClickListener {
                binding.swipe.close(true)
                listener.onBuyProduct(Product)
            }
            binding.btnFavorite.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    onToggleLike(Product, true)
                }
                override fun unLiked(likeButton: LikeButton?) {
                    onToggleLike(Product, false)
                }
            })
            binding.btnFavorite.setOnAnimationEndListener {
                // Solo cuando like se pone a true se lanza este callback
                toggleFavoriteJob?.start()
            }
            binding.btnEdit.setSafeClickListener {
                listener.onEditProduct(Product)
            }
            binding.surface.setSafeClickListener {
                when (binding.swipe.openStatus) {
                    SwipeLayout.Status.Open -> binding.swipe.close(true)
                    SwipeLayout.Status.Close -> binding.swipe.open(true)
                }
            }
        }

        lateinit var Product: Product private set
        fun setup(Product: Product) {
            this.Product = Product
            binding.swipe.dragDistance = 120
            binding.swipe.close(false)
            binding.imageviewProduct.setProductImage(Product)
            binding.labelName.text = Product.name
            binding.labelPrice.text = String.format("%s €", AkbNumberParser.LocaleParser.format(Product.price))
            binding.btnFavorite.isLiked = Product.favorite
        }

        private fun onToggleLike(Product: Product, lazy: Boolean) {
            val start = if (lazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
            toggleFavoriteJob = lifecycleScope.launch(Dispatchers.Main, start = start) {
                if (!lazy) delay(100)
                listener.onFavoriteProduct(Product)
            }
        }
    }

    protected class ProductDiffCallback: DiffUtil.ItemCallback<Product>() {

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
            val binding = RowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductVH(binding, listener)
        }

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.setup(getItem(position))
        }
    }
}
