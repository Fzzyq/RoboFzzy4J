package me.fzzy.robofzzy4j.command.voice

import com.google.auth.oauth2.ServiceAccountJwtAccessCredentials
import com.google.cloud.texttospeech.v1.*
import discord4j.core.`object`.entity.Message
import me.fzzy.robofzzy4j.Bot
import me.fzzy.robofzzy4j.command.Command
import me.fzzy.robofzzy4j.command.CommandCost
import me.fzzy.robofzzy4j.command.CommandResult
import reactor.core.publisher.Mono


object Tts : Command("tts") {

    override val cooldownMillis: Long = 60 * 10 * 1000
    override val votes: Boolean = true
    override val description = "Joins the voice channel and plays text to speech"
    override val args: ArrayList<String> = arrayListOf("text")
    override val allowDM: Boolean = true
    override val price: Int = 4
    override val cost: CommandCost = CommandCost.CURRENCY

    override fun runCommand(message: Message, args: List<String>): Mono<CommandResult> {
        if (args.isNotEmpty()) {
            var text = ""
            for (arg in args) {
                text += " $arg"
            }
            text = text.substring(1)
            val fileName = "cache/${System.currentTimeMillis()}.mp3"
            /*val speech = getTextToSpeech(text) ?: return CommandResult.fail("the text to speech api didnt work ${Bot.SAD_EMOJI}")
            val sound = File(fileName)
            FileUtils.writeByteArrayToFile(sound, speech)
            val userVoiceChannel = message.author.getVoiceStateForGuild(message.guild).channel
            if (userVoiceChannel != null) {
                if (Voice.playTempAudio(userVoiceChannel, sound, true, 1F, 40, 20, message.longID) == null)
                    return CommandResult.fail("im sorry, something went wrong when i tried to do that ${Bot.SAD_EMOJI}")
            } else
                return CommandResult.fail("i cant do that if youre not in a voice channel ${Bot.HAPPY_EMOJI}")*/
        }
        return Mono.just(CommandResult.fail("this command is not implemented in this version of me ${Bot.sadEmoji()}"))
    }

    fun getTextToSpeech(text: String): ByteArray? {
        val voices = arrayListOf(
                "en-US-Wavenet-A",
                "en-US-Wavenet-B",
                "en-US-Wavenet-C",
                "en-US-Wavenet-D",
                "en-US-Wavenet-E",
                "en-US-Wavenet-F"
        )
        return try {
            val settings = TextToSpeechSettings.newBuilder().setCredentialsProvider { ServiceAccountJwtAccessCredentials.fromStream(Bot.TTS_AUTH_FILE.inputStream()) }.build()
            val tts = TextToSpeechClient.create(settings)

            val input = SynthesisInput.newBuilder().setText(text).build()
            val voice = VoiceSelectionParams.newBuilder().setLanguageCode("en-US").setName(voices[Bot.random.nextInt(voices.count())]).build()
            val config = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build()
            val response = tts.synthesizeSpeech(input, voice, config)
            val content = response.audioContent
            content.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}