package kh.org.nbc.common.helper

import java.util.regex.Pattern

fun String.isEmail() : Boolean{
    val emailRegex =
        Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")
    return emailRegex.matcher(this).matches()
}

fun String.isPhone() : Boolean{
    val faxRegex = Pattern.compile("855[\\d]{8,9}")
    return faxRegex.matcher(this).matches()
}

fun String.isUsername(): Boolean{
    val regex = Pattern.compile("^(?! )[a-z_0-9]{1,32}")
    return regex.matcher(this).matches()
}

fun String.isContainSpace(): Boolean{
    val string = Pattern.compile(".[\\S]*")
    return string.matcher(this).matches()
}

fun String.isFax() : Boolean{
    val faxRegex = Pattern.compile("855[\\d]{8,9}")
    return faxRegex.matcher(this).matches()
}
