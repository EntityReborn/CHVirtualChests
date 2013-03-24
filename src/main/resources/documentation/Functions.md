###Table of Contents###

* Events
  * [`virtualchest_closed`][vcclosed]
  * [`virtualchest_opened`][vcopened]
* General Functions
  * [`all_virtualchests`][allvc]
  * [`clear_virtualchest`][clearvc]
  * [`close_virtualchest`][closevc]
  * [`create_virtualchest`][createvc]
  * [`del_virtualchest`][delvc]
  * [`get_virtualchest`][getvc]
  * [`set_virtualchest`][setvc]
  * [`update_virtualchest`][updatevc]
  * [`addto_virtualchest`][addtovc]
  * [`takefrom_virtualchest`][takefromvc]
* Player Functions
  * [`pget_virtualchest`][pgetvc]
  * [`popen_virtualchest`][popenvc]
  * [`pviewing_virtualchest`][pviewingvc]


###Events###

<a id="vcclosed"></a>`virtualchest_closed` - *A player closed a virtual chest.*

*Event data:*

* `player` - Name of the player viewing the chest.

* `chest` - [`@chestdata`][chestdata]

[vcclosed]: #vcclosed

---

<a id="vcopened"></a>`virtualchest_opened` - *A player had a virtual chest shown to them.*

*Event data:*

* `player` - Name of the player viewing the chest.

* `chest` - [`@chestdata`][chestdata]

[vcopened]: #vcopened

---

###General Functions###

<a id="allvc"></a>`all_virtualchests()` - *Get a list of all the known cached virtualchest ids.*

*Returns:* non-associative array of strings relating to chest ids

[allvc]: #allvc

---

<a id="clearvc"></a>`clear_virtualchest(`[`@id`][id]`[, @slot])` - *Remove all items from a virtual chest, or clears one item slot.*

If `@slot` is specified, only that slot is cleared.

*Returns:* nothing

[clearvc]: #clearvc

---

<a id="closevc"></a>`close_virtualchest(`[`@id`][id]`)` - *Close a virtual chest for all of it's viewers.*

*Returns:* nothing

[closevc]: #closevc

---

<a id="createvc"></a>`create_virtualchest(`[`@chestdata`][chestdata]`)` - *Create a virtual chest with specified parameters.*

*Returns:* nothing

[createvc]: #createvc

---

<a id="delvc"></a>`del_virtualchest(`[`@id`][id]`)` - *Delete a virtual chest from cache.*

This will also close the virtualchest for any viewers.

*Returns:* nothing

[delvc]: #delvc

----

<a id="getvc"></a>`get_virtualchest(`[`@id`][id]`)` - *Get the contents and identifier variables for a chest with String identifier `@id`.*

*Returns:* [`@chestdata`][chestdata]

[getvc]: #getvc

---

<a id="setvc"></a>`set_virtualchest(`[`@id`][id]`,` [`@itemarray`][itemarray]`)` - *Set a chest whose id is `@id` to contain the items from `@itemarray`.*

The original contents are replaced entirely.

*Returns:* nothing

[setvc]: #setvc

----

`set_virtualchest(`[`@chestdata`][chestdata]`)` - *Alternate syntax for `set_virtualchest(@id, @itemarray)`.*

*Returns:* nothing

---

<a id="updatevc"></a>`update_virtualchest(`[`@id`][id]`,` [`@itemarray`][itemarray]`)` - *Set a chest whose id is `@id` to include the items from `@itemarray`.*

The original contents are still present if not replaced by the new array.

*Returns:* nothing

[updatevc]: #updatevc

----

`update_virtualchest(`[`@chestdata`][chestdata]`)` - *Alternate syntax for `update_virtualchest(@id, @itemarray)`.*

*Returns:* nothing

----

<a id="addtovc"></a>`addto_virtualchest(`[`@id`][id]`,` [`@itemid`][itemid]`,` `@qty``,` `[,` [`@meta`][meta]`]``)` - *Add item with [`@itemid`][itemid] to a chest whose id is `@id`. You must specify quantity as integer in `@qty`. You can also add meta info in [`@meta`][meta].*

*Returns:* `@qty` minus the number of items actually given, can be 0 if succeeded to add the whole quantity of items

[addtovc]: #addtovc

----

<a id="takefromvc"></a>`takefrom_virtualchest(`[`@id`][id]`,` [`@itemid`][itemid]`,` `@qty``)` - *Take item with [`@itemid`][itemid] from a chest whose id is `@id`. You must specify quantity as integer in `@qty`.*

*Returns:* the number of items actually taken, which will be from 0 to `@qty`

[takefromvc]: #takefromvc

----

###Player Functions###

<a id="pgetvc"></a>`pget_virtualchest([@playername])` - *Get the chest the player is viewing.*

*Returns:* id of chest being viewed, or `null` if the player is not viewing a virtual chest

[pgetvc]: #pgetvc

---

<a id="popenvc"></a>`popen_virtualchest([@playername,]` [`@id`][id]`)` - *Show a player a given virtual chest.*

*Returns:* nothing

[popenvc]: #popenvc

---

<a id="pviewingvc"></a>`pviewing_virtualchest(`[`@id`][id]`)` - *Get the viewers watching a given chest.*

*Returns:* non-associative array of strings relating to player names

[pviewingvc]: #pviewingvc

---

###Glossary###

* <a id="id"></a>`id` - String - *The id of a virtual chest.*

  Always required when working with a chest.
[id]: #id

* <a id="size"></a>`size` - Integer - *A capacity for a virtual chest.*

  Must be a multiple of 9. Values greater than 54 will have graphical issues, but otherwise are accepted and functional. Defaults to `54`.
[size]: #size

* <a id="title"></a>`title` - String - *Displayed title when viewing a virtual chest.*

  Defaults to `Virtual Chest`.
[title]: #title

* <a id="chestdata"></a>`chestdata` - Associative Array - *An array describing a virtual chest in a form that can be used with `get_value()` and `store_value()`.*

  Contains `id` and optionally `size` and `title`, as well as entries for [`@itemdata`][itemdata] whose keys refer to the slot number. Functions that return this will return null values in empty slots.

  `size` defaults to `54`, and `title` defaults to `Virtual Chest`.
[chestdata]: #chestdata

* <a id="itemid"></a>`itemid` - Integer - *An ID of item.*
[itemid]: #itemid

* <a id="itemdata"></a>`itemdata` - Associative Array - *An array describing an itemstack.* At the time of this writing, contains keys `type` (numeric Minecraft item type), `qty` (amount of this item), `data` (any numeric data, such as direction, color, etc), `enchantments` (associative array), [`@meta`][meta] (associative array)
[itemdata]: #itemdata

* <a id="itemarray"></a>`itemarray` - Associative Array - *An array describing a collection of [`@itemdata`][itemdata].* The keys are integers representing the slot.
[itemarray]: #itemarray

* <a id="meta"></a>`meta` - Associative Array - *An array describing a item data.* Please go to http://wiki.sk89q.com/wiki/CommandHelper/Staged/API/set_itemmeta for more details.
[meta]: #meta
