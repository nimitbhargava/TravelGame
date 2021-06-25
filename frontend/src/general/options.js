export const difficulty = Object.freeze( [
    {id: 1, name: "Lihtne"},
    {id: 2, name: "Keskmine"},
    {id: 3, name: "Raske"}
])

export const AgeSpeedMultiplier = Object.freeze(
    {
        "toSix": 0.5,
        "toNine": 0.8,
        "overNine": 1.0
    }
)

/**
 *
 * startDelay: Delay in milliseconds before sentence starts
 * extraTimeAtEachWord: additional time in milliseconds for each word
 */
export const KaraokeOptions = Object.freeze(
    {
        "startDelay": 1500,
        "endDelay": 1500,
        "extraTimeAtEachWord": 100
    }
)