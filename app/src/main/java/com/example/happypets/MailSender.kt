package com.example.happypets

import android.os.AsyncTask
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender(
    private val recipientEmail: String,
    private val subject: String,
    private val body: String
) {

    fun send(callback: (Boolean) -> Unit) {
        SendEmailTask(callback).execute()
    }

    private inner class SendEmailTask(private val callback: (Boolean) -> Unit) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                val properties = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                    put("mail.debug", "true")  // Habilitar la depuración
                }

                val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("happypetsofficialteam@gmail.com", "bzkzvzlwfobylldb")
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress("happypetsofficialteam@gmail.com"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    setSubject("Informacion de tu cuenta")
                    setText(body)
                }

                Transport.send(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            callback(result)
        }
    }
}



