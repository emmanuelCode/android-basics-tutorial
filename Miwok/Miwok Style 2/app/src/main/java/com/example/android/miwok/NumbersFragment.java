package com.example.android.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumbersFragment extends Fragment {

    //handle sound playback for all files
    private MediaPlayer mMediaPlayer;

    //handle the audio focus when playing a sound file
    private AudioManager mAudioManager;


    //this listen for audio focus changes
    private AudioManager.OnAudioFocusChangeListener mAudioManagerListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||focusChange ==
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {


                releaseMediaPlayer();


            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback, because you hold the Audio Focus
                // again!
                // i.e. the phone call ended or the nav directions
                // are finished
                // If you implement ducking and lower the volume, be
                // sure to return it to normal here, as well.
                mMediaPlayer.start();
            }

        }
    };


    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };


    //????
    public NumbersFragment() {
        // Required empty public constructor
    }


    /**
     * this method is like the onCreate of an Activity
     * the setContent view is replace by inflater.inflate()
     * the Fragment lifeCycle is similar to the activity lifeCycle
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.word_list,container,false);

                                  //get the activity to have access to method
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);



        // METHODS THAT USALLY GOES TO ONCREATE IN ACTIVITY GOES HERE
        // THE "THIS" TO REPRESENT AN ACTIVITY IS REPLACE BY "getActivity()"


        //list the Word using an ArrayList -> witch is better for growing and shrinking data
        // Create a list of words
        //why final ?
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("one", "lutti",R.drawable.number_one,R.raw.number_one));
        words.add(new Word("two", "otiiko",R.drawable.number_two,R.raw.number_two));
        words.add(new Word("three", "tolookosu",R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four", "oyyisa",R.drawable.number_four,R.raw.number_four));
        words.add(new Word("five", "massokka",R.drawable.number_five,R.raw.number_five));
        words.add(new Word("six", "temmokka",R.drawable.number_six,R.raw.number_six));
        words.add(new Word("seven", "kenekaku",R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight", "kawinta",R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine", "wo’e",R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten", "na’aacha",R.drawable.number_ten, R.raw.number_ten));


        //this array adapter can't handle our two text layout
        //ArrayAdapter<Word> itemsAdapter = new ArrayAdapter<Word>(this, R.layout.list_item, words);
        //so we build an override a new class extending the adapter

        WordAdapter itemsAdapter = new WordAdapter(getActivity(),words,R.color.category_numbers);

        //BE CAREFUL, HERE WE SPECIFY THE ROOTVIEW WHEN ACCESSING IDS
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // store the array object into a variable
                Word word = words.get(i);

                //release the mediaplayer if we play/click  a new sound
                releaseMediaPlayer();


                // Request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mAudioManagerListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request focus for a short amount of time
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback(AudioManager)

                    //setup the mediaPlayerRessource
                    mMediaPlayer = MediaPlayer.create(getActivity(),word.getAudioResourceID());
                    mMediaPlayer.start();


                    //this method set a listener to the player to know if were done playing
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);


                }




            }
        });








        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mAudioManagerListener);
        }
    }





}
