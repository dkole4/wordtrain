import { useState } from 'react'


export const useField = (type, defaultValue = '', includeReset = true) => {
  const [value, setValue] = useState(defaultValue)

  const onChangeText = (e) => setValue(e.target.value)
  const onChangeDropdown = (e, { value }) => setValue(value)

  const resetValue = () => setValue('')

  const field = [{ type, value }]

  if (type === 'dropdown') {
    field[0].onChange = onChangeDropdown

    if (includeReset)
      return field.concat(resetValue)
    return field
  }

  field[0].onChange = onChangeText
  
  if (includeReset)
    return field.concat(resetValue)
  return field
}