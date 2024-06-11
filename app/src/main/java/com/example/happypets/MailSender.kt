package com.example.happypets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender(private val recipientEmail: String, private val subject: String, private val body: String) {

    suspend fun send() {
        withContext(Dispatchers.IO) {
            try {
                val properties = Properties()
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.starttls.enable"] = "true"
                properties["mail.smtp.host"] = "smtp.gmail.com"
                properties["mail.smtp.port"] = "587"

                val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("happypetsofficialteam@gmail.com", "bzkzvzlwfobylldb")
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress("happypetsofficialteam@gmail.com"))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                message.subject = subject
                message.setText(body)

                Transport.send(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}




