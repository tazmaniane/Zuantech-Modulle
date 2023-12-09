package id.zuantech.appmodule.modules.base

import android.view.LayoutInflater
import android.view.ViewGroup

typealias CustomInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T