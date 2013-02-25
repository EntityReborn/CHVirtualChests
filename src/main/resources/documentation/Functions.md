##Argument and Return  types##
Expected argument types in this document are represented as follows:

* `id` - String - *The id of a virtual chest.* Always required when working with a chest.

* `size` - Integer = *A capacity for a virtual chest.* Must be a multiple of 9. Values greater than 54 will have graphical issues, but otherwise are accepted and functional. Defaults to `54`.

* `title` - String - *Displayed title when viewing a virtual chest.* Defaults to `Virtual Chest`.

* `chestdata` - Associative Array - *An array describing a virtual chest in a form that can be used with `get_value()` and `store_value()`.* Contains `id` and optionally `size` and `title`, as well as entries for slot data whose keys refer to the slot number. Functions that return this will return null values in empty slots.

* `itemdata` - Associative Array - *An array describing an itemstack.* At the time of this writing, contains keys `type` (numeric Minecraft item type), `qty` (amount of this item), `data` (any numeric data, such as direction, color, etc), 'enchantments' (associative array), 'meta' (associative array)

##Functions##

**`void create_virtualchest(@chestdata)`** - *Create a virtual chest with specified parameters.* 

---

**`get_virtualchest(@id)`** - *Get the contents and identifier variables for a chest with String identifier `@id`.* Returns an associative array whose keys returned will include `id`, `size` and `title` as specified above, as well as integer indexed keys for the slots, whose values will be similar to what `pinv()` returns.

---

**`all_virtualchests()`** - *Get a list of all the known cached virtualchest ids.* Returns a non-associative array of strings.

---

**`set_virtualchest(@id, @itemarray)`** and **`set_virtualchest(@chestdata)`**