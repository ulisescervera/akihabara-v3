package com.gmail.uli153.akihabara3.domain.use_cases.product

data class ProductsUseCases(
    val getProductsUseCase: GetProductsUseCase,
    val getDrinksUseCase: GetDrinksUseCase,
    val getFoodsUseCase: GetFoodsUseCase,
    val buyProductUseCase: BuyProductUseCase,
    val createProductUseCase: CreateProductUseCase,
    val deleteProductUseCase: DeleteProductUseCase,
    val toggleFavoriteUseCase: ToggleFavoriteUseCase
)
