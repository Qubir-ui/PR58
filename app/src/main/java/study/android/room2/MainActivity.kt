package study.android.room2


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.security.auth.Subject


class MainActivity : AppCompatActivity() {

    private lateinit var rbStudent: RadioButton
    private lateinit var rbSubject: RadioButton
    private lateinit var spinner: Spinner
    private lateinit var listCaption: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var items: List<String>


    val db by lazy {
        Room.databaseBuilder(
            this,
            SchoolDatabase::class.java, "school.db"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rbStudent = findViewById(R.id.rbStudent)
        rbSubject = findViewById(R.id.rbSubject)
        spinner = findViewById(R.id.spinner)
        listCaption = findViewById(R.id.listCaption)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        rbStudent.setOnClickListener{
            listCaption.text = "Student's subjects"
            val list = mutableListOf<String>()
            for (i in DataExample.students){
                list += i.studentName
            }
            items = list
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, items
            )
            spinner.adapter = adapter
            // так же должен меняться выпадающий список
        }

        rbSubject.setOnClickListener{
            listCaption.text = "Students study"
            val list = mutableListOf<String>()
            for (i in DataExample.subjects){
                list += i.subjectName
            }
            items = list
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, items
            )
            spinner.adapter = adapter
            // также должен меняться выпадающий список
        }
        val students = DataExample.students.map { it.studentName }
        val subject = DataExample.subjects.map { it.subjectName }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                if(items[position] in students){
                    lifecycleScope.launch {
                        recyclerView.adapter = ResultAdapter(db.schoolDao.getSubjectsOfStudent(items[position]))
                    }
                }
                else if(items[position] in subject){
                    lifecycleScope.launch {
                        recyclerView.adapter = ResultAdapter(db.schoolDao.getStudentsOfSubject(items[position]))

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val dao = db.schoolDao;

        lifecycleScope.launch {
            DataExample.directors.forEach { dao.insertDirector(it) }
            DataExample.schools.forEach { dao.insertSchool(it) }
            DataExample.subjects.forEach { dao.insertSubject(it) }
            DataExample.students.forEach { dao.insertStudent(it) }
            DataExample.studentSubjectRelations.forEach { dao.insertStudentSubjectCrossRef(it) }
        }
    }

}