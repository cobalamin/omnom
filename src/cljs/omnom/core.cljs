(ns omnom.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]))

(def app-state
  (atom
   {:notes [{:x 100
             :y 50
             :w 200
             :h 150
             :content "# This is cool\nTite *tite* __tite__\n```js\nfunction add(a, b) {\n  return a + b;\n}\n```"}
            {:x 200
             :y 300
             :w 400
             :h 200
             :content "[Gooooogle](http://google.com)"}
            {:x 500
             :y 20
             :w 200
             :h 150
             :content "# Head\n## lines\n### are cool"}]}))

(defcomponent note-view
  [note owner]

  (init-state
   [_]
   {:mounted false})

  (render
   [_]
   (let [{:keys [x y w h content]} note]
     (when (om/get-state owner :mounted)
       (js/setTimeout #(.highlightBlock js/hljs (om/get-node owner)) 0))
     (dom/div {:class "note"

               :style
               {:left x :top y
                :width w :height h}

               :dangerouslySetInnerHTML {:__html (js/marked content)}})))

  (did-mount
   [_]
   (om/set-state! owner :mounted true)))

(defcomponent root-view
  [app owner]

  (render
   [_]
   (dom/div
    (om/build-all note-view (:notes app)))))

(defn main []
  (om/root
    root-view
    app-state
    {:target (. js/document (getElementById "app"))}))
