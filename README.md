# Java课程项目：实现一个自动保存代码版本的IDEA插件
## 1. 项目简介
  本项目是一个为 IntelliJ IDEA 开发的自动保存代码版本的插件，旨在解决手动保存代码无法自动记录历史版本的问题。通过该插件，开发者可以在不依赖手动操作的情况下，自动保存代码的历史版本，并根据代码编辑情况，智能触发保存行为。用户可以通过该插件查看每个保存点的历史记录，并支持将代码回滚至任何一个历史版本。

  该插件特别适用于开发过程中频繁修改代码的场景，能够有效帮助开发者减少因误操作或失误带来的代码丢失风险，并优化版本管理过程

## 2. 项目功能
- 基于三种策略保存代码版本：
  - Structure（结构变化保存策略）：当代码结构发生重要变化（如函数签名修改、类新增、成员变量变动等）时自动保存。
  - Action（用户行为保存策略）：基于代码编辑操作触发的保存策略，如行数增加、关键字输入（如return;）或文件切换时。
  - Time（定时保存策略）：按照时间间隔触发的定期保存。 
  - 自动保存代码版本：根据不同的触发条件（结构、行为或时间）自动保存代码的当前版本。
  - 查看代码历史版本：提供一个界面查看所有历史版本，并详细展示每次保存的代码差异（diff）。
  - 恢复代码历史版本：支持从历史版本中回滚到指定的某个版本。
  - 删除代码历史版本：用户可以手动选择删除不再需要的历史版本，节省存储空间。
## 3. 项目结构
  - listeners：监听器模块，负责捕捉代码变动、用户操作和时间变化，并根据捕捉到的变化信息触发相应的保存操作。
    - ActionListener：监听用户的代码编辑操作，决定是否触发基于行为的保存策略。
    - DocListener & DocVirtualListener：文档和虚拟文件的监听器，负责捕获文档内容的变化。
    - StructureListener：监听代码结构变化，如函数或类的增删，代码块的重构等。
    - TimeListener：定时监听器，按照固定时间间隔触发保存操作。
    - PatchUpdateListener：用于监测和管理代码差异（patch），帮助构建增量保存记录。
  - services：服务层，处理保存管理、版本记录、Git提交等核心逻辑。
    - AutoSaveManager：管理自动保存策略的核心模块，负责根据不同的条件触发保存行为。
    - VersionManager：管理历史版本的记录与存储。
    - GitManager：处理与 Git 相关的操作，如分支管理与提交。
    - RollbackManager：提供代码回滚功能，帮助用户恢复到之前的代码版本。
    - UIManager：负责用户界面（如工具窗口）的管理，支持查看和操作版本历史。
    - VersionRecord：历史版本记录的具体实现类。
  - startup：插件启动时的初始化逻辑。
    - PluginStartupActivity：负责插件启动时的必要初始化工作，如监听器的注册和策略的配置。
  - strategies：策略模块，根据不同的触发条件定义多种自动保存策略。
    - action（用户行为策略）：
      - BlockEdit：监控代码块的编辑行为。
      - LineCount：根据代码行数的变化触发保存。
      - PrintReturn：检测到return;等关键字输入时触发保存。
      - printImport：监测代码中import语句的修改并保存。
    - structure（代码结构策略）：
      - AddFunctionOrClass：当检测到新增函数或类时触发保存。
      - AddNewMemberVariable：新增类成员变量时的保存策略。
      - DeleteMemberVariable：删除类成员变量时的保存策略。
      - FunctionsignatureChanged：当函数签名被修改时触发保存。
      - RefactoringOrMovingOfCodeblocks：代码块重构或移动时触发保存。
      - RemoveFunctionOrClass：当删除函数或类时触发保存。
    - time（定时策略）：
      - TimeAutoSave：基于固定时间间隔自动触发的保存策略。
  - toolWindow：提供查看和管理历史版本的工具窗口，支持版本回滚和删除操作。

目录结构如下：
```
listeners/
    ActionListener.java
    DocListener.java
    DocVirtualListener.java
    PatchUpdateListener.java
    StructureListener.java
    TimeListener.java
services/
    AutoSaveManager.java
    GitManager.java
    RollbackManager.java
    UIManager.java
    VersionManager.java
    VersionRecord.java
startup/
    PluginStartupActivity.java
strategies/
    action/
        BlockEdit.java
        LineCount.java
        printImport.java
        PrintReturn.java
    structure/
        AddFunctionOrClass.java
        AddNewMemberVariable.java
        DeleteMemberVariable.java
        FunctionsignatureChanged.java
        RefactoringOrMovingOfCodeblocks.java
        RemoveFunctionOrClass.java
    time/
        TimeAutoSave.java
toolWindow/
    - 历史版本展示与管理的界面相关代码
```


## 4.设计与实现细节
该插件基于监听器模式和策略模式实现。不同的监听器捕捉代码编辑过程中的特定事件，而这些事件通过预定义的策略进行分类处理，决定是否触发自动保存行为。以下是几个关键设计策略：
- 监听器模式：listeners 模块中的不同监听器通过监听代码编辑、文档变化、结构调整等事件，提供触发保存的依据。
- 策略模式：strategies 模块定义了不同的自动保存策略，包括用户行为策略、结构变化策略和定时策略。各个策略根据不同的事件触发保存，提升了自动保存的灵活性和扩展性。
- 增量保存与Diff算法：采用java-diff-utils，通过生成差异日志（patch）来记录每次保存的增量，减少保存空间占用并提高版本管理的效率。
## 5. 成员分工
- 何如麟：负责项目的整体架构设计，Diff算法和自动保存策略代码和的编写，项目的测试。
- 臧峻哲：负责实现代码历史版本的查看和恢复功能，以及工具窗口页面的展示。
- 周瑾瑜：负责编写文档。
