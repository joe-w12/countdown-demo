package com.countdowndemo

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    /* Tempo of the recording. */
    private val beatsPerMinute = 75

    /* Total number of beats to record. */
    private val numberOfBeatsToRecord = 10

    /* Calculate the number of milliseconds between each beat. */
    private val msPerBeat = 60000L / beatsPerMinute

    /* Number of times that we should update the progress bar position per second. */
    private val progressBarUpdateFrequency = 100

    /* Calculate the number of milliseconds between each time we should update the progress bar frequency. */
    private val msPerProgressBarUpdate = 1000 / progressBarUpdateFrequency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_start_recording.setOnClickListener {
            launchRecordingCountdown()
        }
    }

    private fun launchRecordingCountdown() {
        // Disable the start button while we are recording.
        button_start_recording.isEnabled = false

        /*
            We use the progress bar in this step to indicate how long remains of this beat.
            We configure the progress bar so that it is full when its value is equal to the time between each beat.
            We configure the progress bar so that it is empty when its value is equal to 0.
         */
        progressbar_countdown.max = msPerBeat.toInt()
        progressbar_countdown.min = 0

        /*
            Configure (and start) countdown timer.
            The first argument how long the timer should take to count down.
                - We set this to the length of 4 beats (4 * msPerBeat)
            The second argument is how long to wait between each call of the onTick function.
                - We set this to how long we want to wait between each update of the progress bar.
        */
        object : CountDownTimer(4L * msPerBeat, msPerProgressBarUpdate.toLong()) {
            /* We use this function to recalculate the number of beats left until the recording starts and to update the progress bar. */
            override fun onTick(millisUntilFinished: Long) {
                /*
                    The number of beats remaining is the number of milliseconds until we have finished counting down from 4 beats, integer divided by the number of milliseconds per beat and add one.
                    For Example:
                        - For a BPM of 80, we have 750ms between each beat.
                        - If millisUntilFinished = 1300ms (1300ms left until the timer is done), and we perform the calculation to get the number of beat remaining:
                            millisUntilFinished / msPerBeat = 1300ms / 750ms = 1.777, but as we are doing integer division we discard everything after the decimal point. So we get 1.
                            1 + 1 = 2 beats left
                */

                textview_countdown.text =
                    "Beats Until Recording: ${millisUntilFinished / msPerBeat + 1}"

                /*
                    We update the progress bar's value to equal the amount of time until the next beat starts.
                    For Example:
                       - For a BPM of 80, we have 750ms between each beat.
                       - If millisUntilFinished = 1300ms (1300ms left until the timer is done), and we perform the calculation:
                            millisUntilFinished % msPerBeat (% means modulo operation)
                            = 1300 % 750 = 550ms until the next beat, so we set the progress bar's value to equal this.
                 */
                progressbar_countdown.progress = (millisUntilFinished % msPerBeat).toInt()
            }

            /* We use this function to start the recording sequence. */
            override fun onFinish() {
                // Ensure the progress bar is empty.
                progressbar_countdown.progress = 0

                // Call the start recording function.
                startRecording()
            }
        }.start()
    }

    private fun startRecording() {
        /*
            We use the progress bar in this step to indicate how long is left of the recording.
            We configure the progress bar so that it is full when its value is equal to the full length of the recording (in milliseconds). This is equal to the numberOfBeatsToRecord * msPerBeat.
            We configure the progress bar so that it is empty when its value is equal to 0 (there is no time left of the recording).
         */
        progressbar_countdown.max = (numberOfBeatsToRecord * msPerBeat).toInt()
        progressbar_countdown.min = 0

        /*
            Configure (and start) a countdown timer.
            The first argument is set to the total time we should be recording for (in milliseconds)
            The second argument is set to how long we want to wait between each update of the progress bar.
        */
        object :
            CountDownTimer(numberOfBeatsToRecord * msPerBeat, msPerProgressBarUpdate.toLong()) {
            /* We use this function to recalculate the number of beats left until the recording finishes and to update the progress bar. */
            override fun onTick(millisUntilFinished: Long) {
                // Same calculation as within launchRecordingCountdown.
                textview_countdown.text = "Beats Remaining: ${millisUntilFinished / msPerBeat + 1}"

                // Set the value of the progress bar to the number of milliseconds until the recording is finished.
                progressbar_countdown.progress = millisUntilFinished.toInt()
            }

            /* We use this function to inform the user that the recording has finished and re-enable the start button. */
            override fun onFinish() {
                textview_countdown.text = "Finished Recording"

                // Ensure the progress bar is empty.
                progressbar_countdown.progress = 0

                // Enable the start button.
                button_start_recording.isEnabled = true
            }
        }.start()
    }
}