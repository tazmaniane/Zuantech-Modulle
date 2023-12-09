package id.zuantech.appmodule.helper

import id.zuantech.appmodule.R


enum class HomeMenuEnum {
    UNKNOWN,
    ACIAN_DINDING,
}

class HomeMenu (
    val icon: Int? = null,
    val enum: HomeMenuEnum = HomeMenuEnum.UNKNOWN,
    val title: String = ""
) {

    fun getMenus() : List<HomeMenu> {
        val data : MutableList<HomeMenu> = ArrayList()
//        data.add(HomeMenu(R.drawable.ic_book, HomeMenuEnum.ACIAN_DINDING, "Acian Dinding"))

        return data
    }
}