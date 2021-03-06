export const Endpoints = Object.freeze( {
    LOGIN: "api/user/login",
    REGISTER: "api/user/register",
    SENTENCE_ADD: "api/sentence/save",
    SENTENCE_UPDATE_GROUP: "api/sentence/updategroup",
    KARAOKE_ADD: "api/karaoke/save",
    KARAOKE_GET_RANDOM_BY_GROUP: "api/karaoke/getRandombyDifficultyGroup/",
    KARAOKE_GET_BY_SENTENCE_ID: "api/karaoke/getSentenceById/",
    SENTENCE_GET_ALL: "api/karaoke/getall",
    RESULT_ADD: "api/karaoke/addResult",
    SENTENCE_GET_RECORDING: "api/sentence/recordings/",
    SENTENCE_GET_TEXTGRID: "api/sentence/textgrids/",
    KARAOKE_GET_RECORDING_FILE_BY_ID: "api/karaoke/recordings/",
    KARAOKE_GET_RECORDINGS_BY_SENTENCE: "api/karaoke/recordings/allBySentence/",
    FEEDBACK_GET_ALL_FEEDBACK: "api/feedback/getUserFeedback/",
})