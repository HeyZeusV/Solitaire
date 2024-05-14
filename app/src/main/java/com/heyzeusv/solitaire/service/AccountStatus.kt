package com.heyzeusv.solitaire.service

import androidx.annotation.StringRes
import com.heyzeusv.solitaire.R

sealed class AccountStatus(@StringRes val message: Int) {
    class Idle : AccountStatus(0)
    class UsernameCheck : AccountStatus(R.string.account_status_username_check)
    class CreateAccount : AccountStatus(R.string.account_status_creating_account)
    class SignIn : AccountStatus(R.string.account_status_sign_in)
    class SignOut : AccountStatus(R.string.account_status_sign_out)
}