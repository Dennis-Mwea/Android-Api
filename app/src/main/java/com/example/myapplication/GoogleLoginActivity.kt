package com.example.myapplication

import android.content.Intent
import android.content.IntentSender
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient

class GoogleLoginActivity : AppCompatActivity() {
    private lateinit var signInButton: SignInButton
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun initViews() {
        signInButton = findViewById(R.id.sign_in_button) as SignInButton

        signInButton.setSize(SignInButton.SIZE_WIDE)

        signInButton.setOnClickListener {
            initializeGoogleSignIn()
        }
    }

    private fun initializeGoogleSignIn() {
        var signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun initGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .addApi(Auth.CREDENTIALS_API)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> handleGoogleSignInResolution(resultCode, data)
        }

        when (requestCode) {
            RC_HINT_REQUEST -> handleEmailHintRequestResolution(resultCode, data)
        }
    }

    private fun handleEmailHintRequestResolution(resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            emailHintRequestCancelled()
        } else {
            emailHintRequestSuccess(data)
        }
    }

    private fun emailHintRequestSuccess(data: Intent?) {
        val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
        credential?.let {
            proceedOnMainScreen(it.id)
        }
    }

    private fun saveCredentials() {

        val emailInvalid: Boolean = emailAddressTextInput.editText?.text.toString().trim().isNullOrEmpty() ?: false
        val passwordInvalid: Boolean = passwordTextInput.editText?.text.toString().trim().isNullOrEmpty() ?: false

        if (emailInvalid) {
            emailRequirementError()
            return
        }

        if (passwordInvalid) {
            passwordRequirementError()
            return
        }

        val credentialToSave: Credential =
            Credential
                .Builder(emailAddressTextInput.editText?.text.toString())
                .setPassword(passwordTextInput.editText?.text.toString().trim())
                .build()

        Auth
            .CredentialsApi
            .save(googleApiClient, credentialToSave)
            .setResultCallback({
                result ->
                handleCredentialSaveResult(result)
            })
    }

    private fun initSmartlockCredentialsRequest() {
        smartlockCredentialsRequest = CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
            .build()
    }

    private fun requestCredentials() {
        Auth
            .CredentialsApi
            .request(googleApiClient, smartlockCredentialsRequest)
            .setResultCallback({ credentialRequestResult ->
                handleCredentialRequestResult(credentialRequestResult)
            })
    }

    private fun handleCredentialRequestResult(credentialRequestResult: CredentialRequestResult) {
        if (credentialRequestResult.status.isSuccess) {
            proceedOnMainScreen(credentialRequestResult.credential.id)
        } else {
            resolveCredentialRequest(credentialRequestResult.status)
        }
    }

    private fun resolveCredentialRequest(status: Status?) {
        if (status?.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
            initiateCredentialRequestResolution(status)
        } else {
            credentialRequestFailure()
        }
    }

    private fun initiateCredentialRequestResolution(status: Status?) {
        try {
            status?.startResolutionForResult(this, RC_CREDENTIALS_REQUEST)
        } catch (sendIntentException: IntentSender.SendIntentException) {
            credentialRequestResolutionFailure()
        }
    }

    private fun initHintRequest() {
        hintRequest = HintRequest.Builder()
            .setHintPickerConfig(
                CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .setPrompt(CredentialPickerConfig.Prompt.SIGN_IN)
                    .build()
            )
            .setEmailAddressIdentifierSupported(true)
            .build()
    }
    view raw
}
