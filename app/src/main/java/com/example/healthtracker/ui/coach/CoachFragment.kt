package com.example.healthtracker.ui.coach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.databinding.FragmentCoachBinding
import com.example.healthtracker.service.AICoachEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoachFragment : Fragment() {

    private var _binding: FragmentCoachBinding? = null
    private val binding get() = _binding!!

    private val conversationHistory = StringBuilder()
    private var currentLang = "en"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoachBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication

        lifecycleScope.launch(Dispatchers.IO) {
            val profile = app.repository.getUserProfileDirect()
            currentLang = profile?.language ?: "en"
            val initialWelcome = when (currentLang) {
                "tr" -> "Koç: Merhaba! Ben VitaLog yapay zeka sağlık koçunuzum. Su içme alışkanlıkları, şekersiz yaşam, aralıklı oruç veya dengeli beslenme hakkında dilediğinizi sorabilirsiniz."
                "es" -> "Coach: ¡Hola! Soy tu asistente de bienestar VitaLog. ¿Cómo puedo ayudarte hoy con nutrición, agua, ayuno o hábitos?"
                "de" -> "Coach: Hallo! Ich bin dein VitaLog-Gesundheitscoach. Wie kann ich dir heute bei Ernährung, Fasten oder Gewohnheiten helfen?"
                "fr" -> "Coach : Bonjour ! Je suis votre coach bien-être VitaLog. Posez-moi vos questions sur la nutrition, l'eau ou le jeûne."
                "pt" -> "Coach: Olá! Sou seu coach de saúde VitaLog. Como posso te ajudar com nutrição, água, jejum ou hábitos hoje?"
                else -> "Coach: Hello! I am your VitaLog health companion. How can I help you today with nutrition, water, intermittent fasting, or habits?"
            }
            withContext(Dispatchers.Main) {
                conversationHistory.append(initialWelcome)
                binding.tvCoachConversation.text = conversationHistory.toString()
            }
        }

        binding.btnSendCoach.setOnClickListener {
            val query = binding.etCoachQuestion.text.toString().trim()
            if (query.isNotEmpty()) {
                handleUserQuery(query)
                binding.etCoachQuestion.text.clear()
            }
        }

        binding.chipWater.setOnClickListener { handleUserQuery("How much water should I drink?") }
        binding.chipSugar.setOnClickListener { handleUserQuery("Tips to reduce sugar intake") }
        binding.chipFasting.setOnClickListener { handleUserQuery("Explain 16:8 intermittent fasting") }
        binding.chipProtein.setOnClickListener { handleUserQuery("What are good protein sources?") }
    }

    private fun handleUserQuery(query: String) {
        conversationHistory.append("\n\nYou: ").append(query)
        val answer = AICoachEngine.answerUserQuestion(query, currentLang)
        conversationHistory.append("\n\nCoach: ").append(answer)
        binding.tvCoachConversation.text = conversationHistory.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
