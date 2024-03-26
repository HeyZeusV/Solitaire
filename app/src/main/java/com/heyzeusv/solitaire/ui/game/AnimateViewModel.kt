package com.heyzeusv.solitaire.ui.game

import androidx.lifecycle.ViewModel
import com.heyzeusv.solitaire.data.LayoutInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimateViewModel @Inject constructor(
    val layoutInfo: LayoutInfo,
) : ViewModel()