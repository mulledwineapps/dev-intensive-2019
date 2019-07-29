package ru.skillbranch.devintensive

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = question.question

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (question == Question.IDLE) question.question to status.color
        else "${replyAnswer(answer)}\n${question.question}" to status.color
    }

    private fun replyAnswer(answer: String): String {
        return when {
            question.answerIsValid(answer).not() -> question.hint
            question.answers.map { a -> a.toLowerCase() }.contains(answer.toLowerCase()) -> {
                question = question.nextQuestion()
                "Отлично - ты справился"
            } else -> {
                if (status.ordinal == Status.values().lastIndex) {
                    setParametersToDefault()
                    "Это неправильный ответ. Давай все по новой"
                } else {
                    status = status.nextStatus()
                    "Это неправильный ответ"
                }
            }
        }
    }

    private fun setParametersToDefault() {
        status = Status.NORMAL
        question = Question.NAME
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255,255,255)),
        WARNING(Triple(255,120,0)),
        DANGER(Triple(255,60,60)),
        CRITICAL(Triple(255,0,0));

        fun nextStatus(): Status =
            if (this.ordinal < values().lastIndex) values()[this.ordinal+1] else this
            //if (this.ordinal < values().lastIndex) values()[this.ordinal+1] else values()[0]
    }

    enum class Question(val question: String, val hint: String, val answers: List<String>) {
        NAME("Как меня зовут?", "Имя должно начинаться с заглавной буквы", listOf("Бендер","bender")) {
            override fun nextQuestion() = PROFESSION
            override fun answerIsValid(answer: String) = answer.getOrNull(0)?.isUpperCase() ?: false
        },
        PROFESSION("Назови мою профессию?", "Профессия должна начинаться со строчной буквы", listOf("сгибальщик","bender")) {
            override fun nextQuestion() = MATERIAL
            override fun answerIsValid(answer: String) = answer.getOrNull(0)?.isLowerCase() ?: false
        },
        MATERIAL("Из чего я сделан?", "Материал не должен содержать цифр", listOf("металл","дерево","metal","iron","wood")) {
            override fun nextQuestion() = BDAY
            override fun answerIsValid(answer: String) = !Regex("[0-9]+").containsMatchIn(answer)
        },
        BDAY("Когда меня создали?", "Год моего рождения должен содержать только цифры", listOf("2993")) {
            override fun nextQuestion() = SERIAL
            override fun answerIsValid(answer: String) = !Regex("[^0-9]+").containsMatchIn(answer)
        },
        SERIAL("Мой серийный номер?", "Серийный номер содержит только цифры, и их 7", listOf("2716057")) {
            override fun nextQuestion() = IDLE
            override fun answerIsValid(answer: String) =
                answer.length == 7 && !Regex("[^0-9]+").containsMatchIn(answer)
        },
        IDLE("На этом все, вопросов больше нет", "", listOf()) {
            override fun nextQuestion() = IDLE
            override fun answerIsValid(answer: String) = true
        };

        abstract fun nextQuestion(): Question
        abstract fun answerIsValid(answer: String): Boolean
    }
}