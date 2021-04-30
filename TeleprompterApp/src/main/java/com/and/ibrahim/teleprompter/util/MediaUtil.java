package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.mvp.model.media.FileMedia;
import com.and.ibrahim.teleprompter.mvp.model.media.FileMediaLastModifiedComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MediaUtil {

    public static final String TAG = "MediaUtil";
    private static FileMedia[] mediaList;
    private static Context appContext;
    static boolean VERBOSE = true;
    static boolean fromGallery = false;
    static double kBDelimiter = Contract.KILO_BYTE;
    static double mBDelimiter = Contract.MEGA_BYTE;
    static double gBDelimiter = Contract.GIGA_BYTE;
    public static FileMedia[] getMediaList(Context ctx, boolean fromGal){
        appContext = ctx;
        fromGallery = fromGal;
        if(fromGallery) {
            sortAsPerLatestForGallery();
        }
        else{
            sortAsPerLatest();
        }
        return mediaList;
    }

    private static void sortAsPerLatestForGallery() {
        File dcimFc = null;
        boolean allLoc = false;
        String phoneLoc = appContext.getResources().getString(R.string.phoneLocation);
        String sdcardLoc = appContext.getResources().getString(R.string.sdcardLocation);
        if(SharedPrefManager.getInstance(appContext).getMediaLocation().equalsIgnoreCase(phoneLoc)) {
            dcimFc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + appContext.getResources().getString(R.string.FC_ROOT));
            Log.d(TAG, "PHONE For Gallery");
        }
        else if(SharedPrefManager.getInstance(appContext).getMediaLocation().equalsIgnoreCase(sdcardLoc)){
            dcimFc = new File(SharedPrefManager.getInstance(appContext).getSdCardPath());
             Log.d(TAG, "SD card path For Gallery = "+
                    SharedPrefManager.getInstance(appContext).getSdCardPath());
        }
        else{
            //Combine ALL media content
            allLoc = true;
            File phoneMedia = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + appContext.getResources().getString(R.string.FC_ROOT));
            File sdcardMedia = new File(SharedPrefManager.getInstance(appContext).getSdCardPath());
            File[] phonemediaFiles;
            File[] sdcardmediaFiles = null;
            //Check for phone media
            Log.d(TAG, "sdcardMedia = "+sdcardMedia);
            if(sdcardMedia != null) {
                Log.d(TAG, "sdcardMedia abs path = " + sdcardMedia.getAbsolutePath());
                Log.d(TAG, "sdcardMedia name = " + sdcardMedia.getName());
            }
            phonemediaFiles = getFilesList(phoneMedia);
            //Check for sd card media
            if(sdcardMedia!=null && !sdcardMedia.getName().trim().equalsIgnoreCase(Contract.EMPTY)) {
                sdcardmediaFiles = getFilesList(sdcardMedia);
            }

            if(phonemediaFiles != null && sdcardmediaFiles != null) {
                concatAllMedia(phonemediaFiles, sdcardmediaFiles);
            }
            else{
                if(phonemediaFiles != null && phonemediaFiles.length > 0){
                    Log.d(TAG, "Stream Phone");
                    mediaList = getSortedList(phonemediaFiles);
                }
                else if(sdcardmediaFiles != null && sdcardmediaFiles.length > 0){
                    Log.d(TAG, "Stream SD Card");
                    mediaList = getSortedList(sdcardmediaFiles);
                }
                else {
                    mediaList = null;
                }
            }
        }
        if(!allLoc) {
            File[] mediaFiles = getFilesList(dcimFc);
            if(mediaFiles != null) {
                mediaList = getSortedList(mediaFiles);
            }
            else{
                mediaList = null;
            }
        }
    }

    private static void concatAllMedia(File[] phonemediaFiles, File[] sdcardmediaFiles){
        File[] allMedia;
        //Iterate phone media
        int allMediaCount = 0;
        if(phonemediaFiles != null) {
            allMediaCount += phonemediaFiles.length;
        }
        if(sdcardmediaFiles != null){
            allMediaCount += sdcardmediaFiles.length;
        }
        Log.d(TAG, "allMediaCount = "+allMediaCount);
        allMedia = new File[allMediaCount];
        int index=0;
        if(phonemediaFiles != null){
            for(File phMed : phonemediaFiles){
                allMedia[index++] = phMed;
            }
        }
        if(sdcardmediaFiles != null){
            for(File sdcdMedia : sdcardmediaFiles){
                allMedia[index++] = sdcdMedia;
            }
        }
        if(allMedia != null) {
            Log.d(TAG, "allMedia length = "+allMedia.length);
            mediaList = getSortedList(allMedia);
        }
        else{
            mediaList = null;
        }
    }

    private static void sortAsPerLatest() {
        File dcimFc;
        if(SharedPrefManager.getInstance(appContext).mediaCountMem()) {
            dcimFc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + appContext.getResources().getString(R.string.FC_ROOT));
            Log.d(TAG, "PHONE");
        }
        else{
            dcimFc = new File(SharedPrefManager.getInstance(appContext).getSdCardPath());
             Log.d(TAG, "SD card path = "+SharedPrefManager.getInstance(appContext).getSdCardPath());
        }
        File[] mediaFiles = getFilesList(dcimFc);
        if(mediaFiles != null) {
            mediaList = getSortedList(mediaFiles);
        }
        else{
            mediaList = null;
        }
    }

    private static File[] getFilesList(File media){
        File[] mediaFiles = media.listFiles();
        if(media.exists() && media.isDirectory() && mediaFiles != null && mediaFiles.length > 0){
            mediaFiles = media.listFiles((file) -> {
                if (!file.isDirectory() && (file.getPath().endsWith(appContext.getResources().getString(R.string.IMG_EXT)) ||
                        file.getPath().endsWith(appContext.getResources().getString(R.string.ANOTHER_IMG_EXT)) ||
                        file.getPath().endsWith(appContext.getResources().getString(R.string.VID_EXT)))) {
                    return true;
                }
                return false;
            });
        }
        return ((mediaFiles != null && mediaFiles.length > 0) ? mediaFiles : null);
    }

    public static boolean deleteFile(FileMedia media) {
        File deleteFile = new File(media.getPath());
        return deleteFile.delete();
    }

    private static FileMedia[] getSortedList(File[] mediaFiles){
        ArrayList<FileMedia> mediaArrayList = new ArrayList<>();
        for (int i = 0; i < mediaFiles.length; i++) {
            FileMedia fileMedia = new FileMedia();
            fileMedia.setPath(mediaFiles[i].getPath());
            fileMedia.setLastModified(mediaFiles[i].lastModified());
            mediaArrayList.add(fileMedia);
        }
        Collections.sort(mediaArrayList, new FileMediaLastModifiedComparator());
        mediaList = mediaArrayList.toArray(new FileMedia[mediaArrayList.size()]);
        return mediaList;
    }

    public static boolean doesPathExist(String path){
        if(fromGallery) {
            sortAsPerLatestForGallery();
        }
        else{
            sortAsPerLatest();
        }
        for(int i=0;i<mediaList.length;i++){
            if(path.equalsIgnoreCase(mediaList[i].getPath())){
                return true;
            }
        }
        return false;
    }

    public static int getPhotosCount(){
        int count = 0;
        if(mediaList != null && mediaList.length > 0){
            for(int i=0;i<mediaList.length;i++){
                if(mediaList[i].getPath().endsWith(appContext.getResources().getString(R.string.IMG_EXT)) ||
                        mediaList[i].getPath().endsWith(appContext.getResources().getString(R.string.ANOTHER_IMG_EXT))){
                    count++;
                }
            }
        }
        return count;
    }

    public static int getVideosCount() {
        int count = 0;
        if (mediaList != null && mediaList.length > 0) {
            for (int i = 0; i < mediaList.length; i++) {
                if (mediaList[i].getPath().endsWith(appContext.getResources().getString(R.string.VID_EXT))) {
                    count++;
                }
            }
        }
        return count;
    }
    
    public static String convertMemoryForDisplay(long fileLength){
        StringBuffer memoryConsumed = new StringBuffer();
        if(fileLength >= kBDelimiter && fileLength < mBDelimiter){
             Log.d(TAG,"KB = "+fileLength);
            double kbconsumed = fileLength/kBDelimiter;
            memoryConsumed.append((Math.floor(kbconsumed * 100.0))/100.0);
            memoryConsumed.append(" ");
            memoryConsumed.append(appContext.getResources().getString(R.string.MEM_PF_KB));
        }
        else if(fileLength >= mBDelimiter && fileLength < gBDelimiter){
             Log.d(TAG,"MB = "+fileLength);
            double mbconsumed = fileLength/mBDelimiter;
            memoryConsumed.append((Math.floor(mbconsumed * 100.0))/100.0);
            memoryConsumed.append(" ");
            memoryConsumed.append(appContext.getResources().getString(R.string.MEM_PF_MB));
        }
        else {
             Log.d(TAG,"GB = "+fileLength);
            double gbconsumed = fileLength/gBDelimiter;
            memoryConsumed.append((Math.floor(gbconsumed * 100.0))/100.0);
            memoryConsumed.append(" ");
            memoryConsumed.append(appContext.getResources().getString(R.string.MEM_PF_GB));
        }
        return memoryConsumed.toString();
    }
}
