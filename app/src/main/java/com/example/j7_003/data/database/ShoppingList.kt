package com.example.j7_003.data.database

import com.example.j7_003.data.database.database_objects.ShoppingItem
import com.example.j7_003.data.database.database_objects.Tag
import com.example.j7_003.system_interaction.handler.StorageHandler
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import kotlin.collections.ArrayList

class ShoppingList : ArrayList<Pair<Tag, ArrayList<ShoppingItem>>>() {
    init {
        StorageHandler.createJsonFile(
            "SHOPPING_LIST",
            "ShoppingList.json"
        )

        fetchList()
    }

    /**
     * Adds a given ShoppingElement to this list, according to its given tag.
     * If no element of the given tag existed before, the list generate a new sublist,
     * beginning with a marker element, to mark the expansion state of the given sublist.
     * After that the given element is added to the newly created sublist and the list is sorted.
     * @param element The element to be added to the list.
     */
    fun add(element: ShoppingItem) {
        this.forEach { e ->         // searching for preexistence of the elements tag
            if (e.first == element.tag) {   // add element to tags sublist and save to file
                e.second.add(element)

                //Added this to sort after adding element
                sortSublist(e.second)
                sortTag(e.first)

                save()
                return
            }
        }

        // TODO erstellen eigener kategorie oder in sonstige packen?
        // aktuell wird eigene kategorie erstellt

        // creating the new sublist for the given tag, beginning with an marker
        super.add(Pair(element.tag, arrayListOf(ShoppingItem(element.tag, false))))

        this.forEach { e ->         // searching the newly added sublist and adding the element
            if (e.first == element.tag) {   // add element to tags sublist and save to file
                e.second.add(element)

                sortSublist(e.second)
                sortTag(e.first)

                save()
            }
        }

        // TODO sort the list as needed
    }

    /**
     * This method checks if the given sublist should be expanded.
     * @param tag The tag of the requested sublist.
     */
    fun isTagExpanded(tag: Tag): Boolean {
       return try {
           this[getTagIndex(tag)].second[0].checked
       } catch(e: Exception) {
           false
       }
    }

    /**
     * Retrieves the checked state of the requested item in this list.
     * @param tag The tag of the sublist.
     * @param sublistPosition The position of the requested item inside the sublist.
     * @return The checked boolean of the item, if the item search fails null is returned
     */
    fun isItemChecked(tag: Tag, sublistPosition: Int): Boolean? {
        return try {
            this[getTagIndex(tag)].second[sublistPosition+1].checked
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Flips the current expansion state markers checked boolean.
     * @param tag The tag of the sublist to flip.
     * @return True if the expansion state markers checked boolean is flipped, false otherwise.
     */
    fun flipExpansionState(tag: Tag): Boolean {
        return try {
            this[getTagIndex(tag)].second[0].checked = !this[getTagIndex(tag)].second[0].checked
            save()
            true
        } catch(e: Exception) {
            false
        }
    }

    /**
     * Flips the checked boolean of the requested item.
     * @param tag The tag of the sublist.
     * @param sublistPosition The position of the requested item.
     * @return The new index of the requested item, -1 if the flipping process fails
     */
    fun flipItemCheckedState(tag: Tag, sublistPosition: Int): Int {
        return try {
            val itemCache: ShoppingItem = this[getTagIndex(tag)].second[sublistPosition+1]
            itemCache.checked = !itemCache.checked
            sortSublist(this[getTagIndex(tag)].second)
            save()
            this[getTagIndex(tag)].second.indexOf(itemCache) - 1
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Tries to fetch the length of the sublist with given tag if the sublist exists.
     * @param tag The tag the sublist is supposed to have.
     * @return Either the size of the list or zero if it doesn't exist.
     */
    fun getSublistLength(tag: Tag): Int{
        this.forEach {
            if(it.first == tag){
                return it.second.size - 1
            }
        }
        return 0
    }

    /**
     * Retrieves the amount of unchecked items of a given sublist.
     * @param tag The tag of the sublist which will be searched.
     * @return The amount of unchecked items in the given tags sublist.
     */
    fun getUncheckedSize(tag: Tag): Int {
        var counter: Int = 0
        for (i in 1 until this[getTagIndex(tag)].second.size) {
            if (!this[getTagIndex(tag)].second[i].checked) {
                counter++
            }
        }
        return counter
    }

    /**
     * Retrieves an item from a given tags sublist by position without deleting it.
     * @param tag The tag of the needed sublist.
     * @param subPosition The position of the requested item inside the sublist.
     * @return If the item could be found it is returned, null otherwise.
     */
    fun getItem(tag: Tag, subPosition: Int): ShoppingItem? {
        this.forEach{
            if(it.first == tag){
                return it.second[subPosition+1]
            }

        }
        return null
    }

    /**
     * Gathers all tags present in this list. An empty list is returned if there are no tags.
     * @return A list containing all tags of this list.
     */
    fun getTags(): ArrayList<Tag> {
        val tagList: ArrayList<Tag> = ArrayList()
        this.forEach { e ->
            tagList.add(e.first)
        }
        return tagList
    }

    fun areAllChecked(tag: Tag) : Boolean {
        return try {
            getUncheckedSize(tag) == 0
        } catch (e: Exception) {
            false
        }
    }
    
     fun getTagIndex(tag: Tag): Int {
        for (i in 0 until this.size) {
            if (this[i].first == tag) {
                return i
            }
        }

        return -1
    }

    /**
     * Tries to remove an item from the list, if there are no items left in the given category,
     * the whole sublist is removed. Depending on the outcome of the removal either the removed
     * item is returned or null.
     * @param tag The tag of the sublist.
     * @param sublistPosition The position of the item inside the sublist.
     * @return  The removed item is returned if the removal succeeded, null otherwise.
     *          also a boolean is returned, stating if the containing sublist was deleted or not.
     */
    fun removeItem(tag: Tag, sublistPosition: Int): Pair<ShoppingItem?, Boolean> {
        var removedItem: ShoppingItem? = null
        var sublistGotDeleted = false


        return try {    // trying to remove the item, save the list and return the removed element
            for (i in 0 until this.size) {

                if (this[i].first == tag) {
                    removedItem = this[i].second.removeAt(sublistPosition + 1)
                    if (this[i].second.size==1) {   // removing the sublist if it is empty
                        super.remove(this[i])
                        sublistGotDeleted = true
                    }

                    break
                }
            }

            save()
            Pair(removedItem, sublistGotDeleted)
        } catch (e: NullPointerException) {
            Pair(null, sublistGotDeleted)
        }
    }

    /**
     * Sorts the given list. Sublists where all items checked are sorted below.
     * If sorting the list succeeds a pair is returned containing the prior and new
     * position of the sorted tag.
     * @param tag The tag which should get sorted.
     * @return A pair with the old and new position of the given tag, null if sorting fails.
     */
    fun sortTag(tag: Tag): Pair<Int, Int>? {
        val oldPosition = getTagIndex(tag)
        this.sortBy { areAllChecked(it.first) }
        save()
        val returnPair = Pair(oldPosition, getTagIndex(tag))

        return if (returnPair.first == returnPair.second) {
            null
        } else {
            returnPair
        }
    }

    private fun sortSublist(list: ArrayList<ShoppingItem>) {
        val markerList: ArrayList<ShoppingItem> = arrayListOf(list[0])
        list.remove(markerList[0])
        list.sortWith(compareBy({it.checked}, {it.name}))
        markerList.addAll(list)
        list.clear()
        list.addAll(markerList)
    }

    private fun save() {
        StorageHandler.saveAsJsonToFile(
            StorageHandler.files["SHOPPING_LIST"],
            this
        )
    }

    private fun fetchList() {
        val jsonString = StorageHandler.files["SHOPPING_LIST"]?.readText()

        this.addAll(GsonBuilder().create()
            .fromJson(jsonString,
                object : TypeToken<ArrayList<Pair<Tag, ArrayList<ShoppingItem>>>>() {}.type))
    }
}