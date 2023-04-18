package com.cheesecake.todo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cheesecake.todo.R
import com.cheesecake.todo.data.models.HomeItem
import com.cheesecake.todo.data.models.Tag
import com.cheesecake.todo.data.models.TodoItem
import com.cheesecake.todo.data.repository.todos.TodoRepositoryFactory
import com.cheesecake.todo.databinding.FragmentHomeBinding
import com.cheesecake.todo.ui.base.BaseFragment
import com.cheesecake.todo.ui.taskDetails.TaskDetailsFragment
import com.cheesecake.todo.ui.viewall.ViewAllTodoItemsFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeView {

    override val bindingInflater: (LayoutInflater) -> FragmentHomeBinding =
        FragmentHomeBinding::inflate
    private lateinit var presenter: HomePresenter
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        setUp()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setUp() {
        val todoFactory = requireActivity().application as TodoRepositoryFactory
        presenter = HomePresenter(todoFactory.createTodoRepository())
        presenter.attachView(this)
        presenter.initTodos()
        homeAdapter = HomeAdapter(::loadDetailsFragment, ::loadViewAllFragment)
        binding.recyclerViewHome.adapter = homeAdapter
    }

    override fun initHomeList(homeItem: HomeItem) {
        requireActivity().runOnUiThread {
            val todosList = listOf(
                DataItem.Header(
                    homeItem.personalTodoPercentage,
                    homeItem.personalProgressPercentage,
                    homeItem.personalDonePercentage
                ),
                DataItem.TagItem(Tag(0, "Recently Personal", homeItem.personalTodos)),
                DataItem.TagItem(Tag(1, "Recently Team", homeItem.teamTodo))
            )
            homeAdapter.submitList(todosList)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun loadViewAllFragment(todoTitle: String) {
        val isPersonal = todoTitle.contains("Personal")
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_container_activity,
                ViewAllTodoItemsFragment.newInstance(isPersonal)
            )
            addToBackStack(null)
            commit()
        }
    }

    private fun loadDetailsFragment(todoItem: TodoItem) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_activity, TaskDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("todoItem", todoItem)
                }
            })
            addToBackStack(null)
            commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }


}