# MaintenanceManager
Software that manages the maintenance of a diesel engine.

This software (actually, it is not yet a software because I didn't write any documentation) manages the maintenance of my diesel engine.
I'm using it for 2 years and it makes the maintenance of my inboard engine straightforward.

This software developped in Java allow to :
-Create, edit, remove maintenance task.
-Display the different maintenance task in a table with different color to have a quick overview about the maintenance task to do :
  -green, the task is OK
  -yellow, the maintenance task has to be done soon
  -red, the task has to be done as soon as possible
  
-When you do the maintenance task, you can input the entry in the software with a comment.
-You can visualize the history of a task and then see how often, when has been done a task. You can also visualize all the comments about your work done.

-You can visualize an whole history of the engine maintenance in one tab.

-Every task are editable, so by this way, you can add some details about the task to make the work relative to the task easier.

Everything (tasks and entries) are saved in a XML file. 

If you want to use this software without compiling everything, you can download the content of EngineMaintenance directory. A jar file can be launched with the bat file if you have installed java 8.0.
The file EngineMonitor.xml contains your maintenance plan and all the history of the maintenance done on your engine. The file EngineMonitor.xml in GitHub contains some examples of task. You will have probably remove some to add yours.

Feedback would be appreciated !

