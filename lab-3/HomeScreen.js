import React from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import { API_URL } from '@env';

const HomeScreen = () => {

  const [data, setData] = useState( [] );
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState( '' );
  const [description, setDescription] = useState( '' );

  useEffect(()=> {
    const fetchData = async () => {
      try {
        const response = await fetch(`${API_URL}/tasks`);
        const result = await response.json();
        setData(result);
      } catch(error){
        console.error(error);
      } finally{
        setLoading(false);
      }
    };
    fetchData();
  });

  const renderItem = ({item}) => {
  const date = new Date(item.createdAt);
  const formattedDate = date.toLocaleDateString();
  const formattedTime = date.toLocaleTimeString();
    return(
      <View key={item._id} style={styles.itemContainer}>
        <Text style={styles.title}>{item.title}</Text> 
        <Text style={styles.description}>{item.description}</Text>
        <Text style={styles.meta}>By: {item.user} - Comments: {item.commentCount}</Text>
        <Text style={styles.meta}>{formattedDate} {formattedTime}</Text>
      </View>);
  };

  const handleSubmit = async () =>{
    if (title === '' || description === ''){
      alert('Please fill in both title and desc.');
      return;
    }
    try{
      const response = await fetch(`${API_URL}/task`, {
        method: 'POST',
        header: {
          'Content-Type' :  'application/json'
        },
        body: JSON.stringify({title, description})
      });
      const newTask = response.json();
      setData((prevData) => [...prevData,  newTask]);
      setTitle('');
      setDescription('');
    } catch(error){
      console.error(error);
    }
  };

  if (loading) {
    return (
      <View style={styles.container}>
        <ActivityIndicator size="large" color="#0000ff" />
      </View>
    );
  }
  
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Todo List</Text>
      <FlatList
        data={data}
        renderItem={renderItem}
        keyExtractor={(item) => item._id}
      />
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          placeholder="Title"
          value={title}
          onChangeText={setTitle}
        />
        <TextInput
          style={styles.input}
          placeholder="Description"
          value={description}
          onChangeText={setDescription}
        />
        <TouchableOpacity style={styles.button} onPress={handleSubmit}>
          <Text style={styles.buttonText}>Submit</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
  

  const styles = StyleSheet.create({
    container: {
      flex: 1,
      padding: 10,
    },
    header: {
      fontSize: 24,
      fontWeight: 'bold',
      marginVertical: 10,
      textAlign: 'center',
    },
    itemContainer: {
      padding: 10,
      marginVertical: 5,
      borderWidth: 1,
      borderColor: '#ccc',
      borderRadius: 5,
    },
    title: {
      fontSize: 18,
      fontWeight: 'bold',
    },
    description: {
      fontSize: 14,
      marginVertical: 5,
    },
    meta: {
      fontSize: 12,
      color: '#666',
    },
  });
  

   export default HomeScreen;
