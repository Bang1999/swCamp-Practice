package com.rhythmfinders.domain.post.repository;

import com.rhythmfinders.domain.post.aggregate.Post;
import com.rhythmfinders.domain.post.stream.MyObjectOutput;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PostRepository {
    private static ArrayList<Post> postListInstance = new ArrayList<>();
    final String postDBPath = "src/main/java/com/rhythmfinders/domain/post/db/postDB.dat";

    public PostRepository() {

        File file = new File(postDBPath);
        System.out.println(file);

        if (!file.exists()) {
            postListInstance.add(
                    new Post(1, "title01", "contentsDetail01", "member01", LocalDateTime.now(), LocalDateTime.now(), 1)
            );
            postListInstance.add(
                    new Post(2, "title02", "contentsDetail02", "member02", LocalDateTime.now(), LocalDateTime.now(), 10000)
            );
            postListInstance.add(
                    new Post(3, "title03", "contentsDetail03", "member03", LocalDateTime.now(), LocalDateTime.now(), 100)
            );
            savePosts(postListInstance);
        }

        loadPost(postDBPath);
        for (Post post : postListInstance) {
            System.out.println(post);
        }
        System.out.println();
    }

    private static void loadPost(String postDBPath) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(postDBPath)));

            while(true) {
                postListInstance.add((Post) ois.readObject());
            }
        } catch (EOFException e) {
            System.out.println("read completely");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void savePosts(ArrayList<Post> posts) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("src/main/java/com/rhythmfinders/domain/post/db/postDB.dat")));
            for (Post post : posts) {
                oos.writeObject(post);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int insertPost(Post newPost) {
        MyObjectOutput moo = null;
        int result = 0;
        try {
            moo = new MyObjectOutput(new BufferedOutputStream(new FileOutputStream(postDBPath, true)));

            moo.writeObject(newPost);
            postListInstance.add(newPost);
            result = 1;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (moo != null) moo.close();
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    public ArrayList<Post> selectAllPost() {
        ArrayList<Post> resultSet = (ArrayList<Post>) postListInstance.clone();

        return resultSet;
    }

    public int deletePost(int postId) {
        int result = 0;
        for (Post post : postListInstance) {
            if (post.getPostId() == postId) {
                postListInstance.remove(post);
                savePosts(postListInstance);
                result = 1;
                return result;
            }
        }
        return result;
    }

    public int updatePost(Post reform) {
        reform.setUpdateDate(LocalDateTime.now());
        int result = 0;
        for (int i = 0; i < postListInstance.size(); i++) {
            if (postListInstance.get(i).getPostId() == reform.getPostId()) {
                postListInstance.set(i, reform);
                savePosts(postListInstance);
                result = 1;
                return result;
            }
        }
        return 0;
    }

    public Post selectPost(int postId) {
        for (Post post : postListInstance) {
            if (post.getPostId() == postId) {
                return new Post(
                        postId,
                        post.getPostTitle(),
                        post.getPostContents(),
                        post.getMember(),
                        post.getCreateDate(),
                        post.getUpdateDate(),
                        post.getView()
                );
            }
        }
        return null;
    }

    public int selectLastPostNo() {
        Post lastPost = postListInstance.get(postListInstance.size() - 1);
        return lastPost.getPostId() + 1;
    }
}
