package com.example.lantravel.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.lantravel.R
import com.example.lantravel.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri?=null

    var storage:FirebaseStorage?=null
    var auth:FirebaseAuth?=null
    var firestore:FirebaseFirestore?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate 스토리지 초기화
        storage= FirebaseStorage.getInstance()
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        //OPEN the album
        var photoPickerIntent=Intent(Intent.ACTION_PICK)
        photoPickerIntent.type="image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contnetUpload()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image 이미지의 경로가 넘어옴
                photoUri=data?.data
                addphoto_image.setImageURI(photoUri)
            }else{

                //취소버튼 누르면 작동하는 부분
                finish()
            }
        }
    }
    fun contnetUpload(){
        //파일이름 만들기
        var timestamp=SimpleDateFormat("yyyyMMss_HHmmss").format(Date())//이름이 중복되지 않도록 날짜를 파일명으로
    var imageFileName="IMAGE_"+timestamp+"_.png"

        var storageRef=storage?.reference?.child("image")?.child(imageFileName)

        //promise method(callback method 중에 골라서 쓰기 이 사람은 구글이 권장해서 이거 씀)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()
            //insert uid of user
            contentDTO.uid = auth?.currentUser?.uid
            //insert userid
            contentDTO.userId = auth?.currentUser?.email
            //insert explain of content
            contentDTO.explain=addphoto_edit_explain.text.toString()
            //insert timestamp
            contentDTO.timestamp=System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }

        /*Callback method
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
            }*/

        }

    }
