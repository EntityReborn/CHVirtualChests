###Simple Virtual Chests Example

Place the following code blocks in the mentioned files.

In `main.ms`:

    _load_vcs()
    
    bind('virtualchest_closed', null, null, @event,
        @id = @event['chest']['id']
        @chest = get_virtualchest(@id)
        store_value(@id, @chest)
    )
  
---

In `config.txt`:

    *:/vc [$]= >>>
        @id = 'vc.players.' . player()
        @chest = get_value(@id)
        @chest['title'] = @id
        @chest['id'] = @id

        create_virtualchest(@chest)
        popen_virtualchest(@id)
    <<<

---

In `auto_include.ms`:

    proc(_load_vcs,
        @vcs = get_values('vc')

        foreach(@vcs, @vc,
            @vc['title'] = @vc['id']
            console('Loading virtual chest' @vc['id'])
            create_virtualchest(@vc)
        )
    )

The player can call their virtual chest at any time by typing `/vc`.