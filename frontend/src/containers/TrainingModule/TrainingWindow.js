import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { 
  Container, Button, Input, Segment, Grid, Divider, Header 
} from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { setErrorMessage, setNotificationMessage } from 'store/actions/notification-actions'
import { updateScore } from 'store/actions/word-actions'
import { useField } from 'utils/useField'


const getRandomValue = (id) => 
  Math.floor(Math.random() * id)

const getRandomizedWords = (words) =>
  words.sort((a, b) => getRandomValue(a.id) - getRandomValue(b.id))

export const TrainingWindow = ({ words, resetModule, trainingLanguage }) => {
  const [current, setCurrent] = useState(0)
  const [score, setScore] = useState(0)
  const [changes, setChanges] = useState([])
  const [answer, resetAnswer] = useField('text')
  const [shuffled, setShuffled] = useState(getRandomizedWords([...words]))
  
  const user = useSelector(state => state.loggedUser)
  
  const dispatch = useDispatch()

  const handleKeyPress = (e) => {
    if (e.charCode === 13) {  // Proceed to the next word if Enter was pressed.
      e.preventDefault()
      checkAnswer()
    }
  }

  const checkAnswer = () => {
    const correctAnswer = (trainingLanguage === shuffled[current].langTranslation) 
      ? shuffled[current].translation
      : shuffled[current].word
    
    if (correctAnswer.split('/').includes(answer.value)) {
      dispatch(
        setNotificationMessage(
          `Correct! Your score: ${score+1} / ${current+1}`
        )
      )
      
      setScore(score + 1)
      setChanges(
        changes.concat(
          {
            wordId: shuffled[current].id,
            userId: user.id,
            tries: shuffled[current].tries + 1, 
            score: shuffled[current].score + 1
          }
        )
      )
    } else {
      dispatch(
        setErrorMessage(
          `Wrong. Correct answer: ${correctAnswer}`
        )
      )
      
      setChanges(
        changes.concat(
          {
            wordId: shuffled[current].id,
            userId: user.id,
            tries: shuffled[current].tries + 1, 
            score: shuffled[current].score
          }
        )
      )
    }
    resetAnswer()
    setCurrent(current + 1)
  }

  const finishRound = () => {
    setShuffled([])
    dispatch(updateScore(user, changes))
    resetModule()
  }

  if (current >= shuffled.length) {
    return (
      <Container>
        <Header>Your score: {score} / {words.length}</Header>
        <Button positive onClick={finishRound}>Finish session</Button>
      </Container>
    )
  }

  return (
    <Container>
      <Segment placeholder>
        <Grid columns={2} stackable textAlign='center'>
          <Divider vertical />

          <Grid.Row>
            <Grid.Column>
              <Header as='h1'>
                { trainingLanguage !== shuffled[current].langWord 
                  ? shuffled[current].word
                  : shuffled[current].translation
                }
              </Header>
            </Grid.Column>

            <Grid.Column>
              <Input 
                onKeyPress={handleKeyPress}
                fluid
                {...answer}
                placeholder='Write the translation...' />
            </Grid.Column>
          </Grid.Row>
          
          <Grid.Row>
            <Grid.Column>
              <Header disabled>
                { trainingLanguage !== shuffled[current].langWord 
                  ? shuffled[current].langWord
                  : shuffled[current].langTranslation
                }
              </Header>
            </Grid.Column>
            
            <Grid.Column>
              <Header disabled>
                { trainingLanguage === shuffled[current].langWord 
                  ? shuffled[current].langWord
                  : shuffled[current].langTranslation
                }
              </Header>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Segment>
    </Container>
  )
}

TrainingWindow.propTypes = {
  words: PropTypes.array.isRequired,
  resetModule: PropTypes.func.isRequired,
  trainingLanguage: PropTypes.string.isRequired
}