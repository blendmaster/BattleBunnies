Battle Bunnies
==============

Yes, it's just about how you may be imagining it. You know that [Jump 'n Bump](http://en.wikipedia.org/wiki/Jump_'n_Bump) game? It's like that, only inefficiently coded in java by a high-schooler.

I was that high-schooler once.

But it is still totes amazing. Custom levels, awesome graphics, cute bunnies. 

L2PLAY
------

Use the jar file, or if you're feeling adventurous, you can open the source folder in [BlueJ](http://www.bluej.org/) (leet coder IDE)

So there's some semblance of an interface, but my 16-year-old mind couldn't yet comprehend subjects like 'usability'. Thus, under the aptly-named 'File' menu, you can create a new game.

The possible maps are:

*Arena
*Colliseum (yes, l2spell I know)
*Jungle
*Testing (empty map that turns off screen redraws, it's kinda trippy)

The rest of the dialogs should be straightforward.

The controls are:

*Player 1: Arrow Keys
*Player 2: WASD
*Player 3: IJKL
*Player 4: 8456 (on the numpad preferrably)

Custom maps
-----------

Technically, you can make them. There's a map file for each map under the /maps directory. They're actually just text files which describe the tiles of the map. Should be straightforward enough.

The maps also need a <mapname>-f.png and <mapname>-b.png under the /images directory. -f means foreground, and -b is background. Obvious, right?

The new game dialog handles the filenames, so you should be able to call your maps whatever you want.

The code
--------

It is sad and stuff. Viewer discretion advised.
