package com.example.android.miwok;

/**
 *
 *{@link Word} represents a vocabulary word that the user wants to learn.
 * It contains a default translation and a Miwok translation for that word.
 */

public class Word {


    private String mMiwokTranslation;
    private String mDefaultTranslation;
    private int mImageResourceID = NO_IMAGE_PROVIDED;

    private static final int NO_IMAGE_PROVIDED = -1;

    private int mAudioResourceID;


    /**
     * Create a new Word object.
     *
     * @param defaultTranslation is the word in a language that the user is already familiar with
     *                           (such as English)
     * @param miwokTranslation is the word in the Miwok language
     */
    public Word(String defaultTranslation, String miwokTranslation, int soundID ){
        mMiwokTranslation = miwokTranslation;
        mDefaultTranslation = defaultTranslation;
        mAudioResourceID = soundID;


    }


    /**
     * Create a new Word object.
     *
     * @param defaultTranslation is the word in a language that the user is already familiar with
     *                           (such as English)
     * @param miwokTranslation is the word in the Miwok language
     */
    public Word(String defaultTranslation, String miwokTranslation, int imageResourceID, int soundID){
        mMiwokTranslation = miwokTranslation;
        mDefaultTranslation = defaultTranslation;
        mImageResourceID = imageResourceID;
        mAudioResourceID = soundID;


    }





    /**
     * Get the miwok translation of the word.
     */
    public String getMiwokTranslation(){

        return mMiwokTranslation;
    }

    /**
     * Get the default translation of the word.
     */
    public String getDefaultTranslation(){

        return mDefaultTranslation;
    }

    public int getImageResourceID(){
        return mImageResourceID;
    }

    public boolean hasImage(){
       return mImageResourceID != NO_IMAGE_PROVIDED;
    }




    public int getAudioResourceID(){

        return mAudioResourceID;

    }

}
